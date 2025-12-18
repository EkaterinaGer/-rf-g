package searchengine.services;

import searchengine.model.*;
import searchengine.repository.*;
import searchengine.util.Lemmatizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

@Service
public class IndexingService {
    private static final Logger logger = LoggerFactory.getLogger(IndexingService.class);
    
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    
    private final Lemmatizer lemmatizer = new Lemmatizer();
    
    public IndexingService(SiteRepository siteRepository, PageRepository pageRepository,
                           LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }
    
    /**
     * Индексирует отдельную страницу
     */
    @Transactional
    public void indexPage(String url) {
        try {
            String siteUrl = extractSiteUrl(url);
            if (!url.startsWith(siteUrl)) {
                throw new IllegalArgumentException("Страница не принадлежит указанному сайту");
            }
            
            SiteTable site = siteRepository.findByUrl(siteUrl)
                    .orElseGet(() -> siteRepository.save(new SiteTable(siteUrl, extractSiteName(siteUrl))));
            
            String path = extractPath(url, siteUrl);
            
            Optional<SitesPageTable> existingPage = pageRepository.findBySiteAndPath(site, path);
            existingPage.ifPresent(this::deletePageData);
            
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .referrer("http://www.google.com")
                    .timeout(10000)
                    .get();
            
            int statusCode = doc.connection().response().statusCode();
            if (statusCode >= 400) {
                logger.warn("Пропущена страница с кодом ошибки {}: {}", statusCode, url);
                return;
            }
            
            String html = doc.html();
            String cleanText = lemmatizer.cleanHtml(html);
            
            SitesPageTable page = pageRepository.save(new SitesPageTable(site, path, statusCode, html));
            
            Map<String, Integer> lemmas = lemmatizer.getLemmas(cleanText);
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemmaText = entry.getKey();
                Integer count = entry.getValue();
                
                Lemma lemma = lemmaRepository.findByLemma(lemmaText)
                        .orElseGet(() -> lemmaRepository.save(new Lemma(lemmaText, 0)));
                
                if (indexRepository.findByPage(page).stream().noneMatch(idx -> idx.getLemma().equals(lemma))) {
                    lemma.setFrequency(lemma.getFrequency() + 1);
                    lemmaRepository.save(lemma);
                }
                
                indexRepository.save(new SearchIndex(page, lemma, count.floatValue()));
            }
            
            logger.info("Страница проиндексирована: {}", url);
            
        } catch (IOException e) {
            logger.error("Ошибка при загрузке страницы {}: {}", url, e.getMessage(), e);
            throw new RuntimeException("Не удалось загрузить страницу: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Ошибка при индексации страницы {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional
    private void deletePageData(SitesPageTable page) {
        indexRepository.findByPage(page).forEach(index -> {
            Lemma lemma = index.getLemma();
            indexRepository.delete(index);
            lemma.setFrequency(Math.max(0, lemma.getFrequency() - 1));
            if (lemma.getFrequency() == 0) {
                lemmaRepository.delete(lemma);
            } else {
                lemmaRepository.save(lemma);
            }
        });
        pageRepository.delete(page);
    }
    
    private String extractSiteName(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host != null && host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host != null ? host : url;
        } catch (URISyntaxException e) {
            return url;
        }
    }
    
    private String extractSiteUrl(String pageUrl) {
        try {
            URI uri = new URI(pageUrl);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный URL");
        }
    }
    
    private String extractPath(String pageUrl, String siteUrl) {
        try {
            URI pageUri = new URI(pageUrl);
            String path = pageUri.getPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }
            String query = pageUri.getQuery();
            if (query != null && !query.isEmpty()) {
                path += "?" + query;
            }
            return path;
        } catch (URISyntaxException e) {
            return pageUrl.replace(siteUrl, "");
        }
    }
}
