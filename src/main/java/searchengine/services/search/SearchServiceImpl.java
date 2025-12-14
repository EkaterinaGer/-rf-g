package searchengine.services.search;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import searchengine.dto.SearchDto;
import searchengine.dto.responses.ResultDto;
import searchengine.lemma.LemmaEngine;
import searchengine.model.Lemma;
import searchengine.model.SearchIndex;
import searchengine.model.SitesPageTable;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final PageRepository pageRepository;

    public SearchServiceImpl(LemmaRepository lemmaRepository,
                             IndexRepository indexRepository,
                             PageRepository pageRepository) {
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.pageRepository = pageRepository;
    }

    @Override
    public List<ResultDto> search(SearchDto searchDto) {
        String query = searchDto.getQuery();
        String siteFilter = searchDto.getSite();
        int pageNumber = Math.max(searchDto.getPage(), 1);
        int pageSize = Math.max(searchDto.getSize(), 10);

        if (!StringUtils.hasText(query)) return Collections.emptyList();

        List<String> queryLemmas = new ArrayList<>(LemmaEngine.getLemmas(query).keySet());
        if (queryLemmas.isEmpty()) return Collections.emptyList();

        List<Lemma> lemmasFromDb = lemmaRepository.findByLemmaIn(queryLemmas);
        if (lemmasFromDb.isEmpty()) return Collections.emptyList();

        List<Integer> lemmaIds = lemmasFromDb.stream().map(Lemma::getId).collect(Collectors.toList());

        List<SearchIndex> indices;
        if (siteFilter != null && !siteFilter.isBlank()) {
            indices = indexRepository.findByLemmaIdInAndPage_Site_Url(lemmaIds, siteFilter);
        } else {
            indices = indexRepository.findByLemmaIdIn(lemmaIds);
        }

        Map<SitesPageTable, Double> pageRelevance = new HashMap<>();
        for (SearchIndex index : indices) {
            SitesPageTable page = index.getPage();
            pageRelevance.put(page, pageRelevance.getOrDefault(page, 0.0) + index.getRank());
        }

        List<ResultDto> sortedResults = pageRelevance.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(entry -> {
                    SitesPageTable page = entry.getKey();
                    double relevance = entry.getValue();
                    String snippet = generateSnippet(page.getContent(), queryLemmas, 150);
                    return new ResultDto(page.getPath(), snippet, relevance);
                })
                .collect(Collectors.toList());

        int fromIndex = Math.min((pageNumber - 1) * pageSize, sortedResults.size());
        int toIndex = Math.min(fromIndex + pageSize, sortedResults.size());
        return sortedResults.subList(fromIndex, toIndex);
    }

    private String generateSnippet(String content, List<String> lemmas, int maxLength) {
        if (content == null) return "";
        content = content.replaceAll("\\<.*?\\>", " ");
        int index = -1;
        for (String lemma : lemmas) {
            index = content.toLowerCase().indexOf(lemma.toLowerCase());
            if (index >= 0) break;
        }
        if (index == -1) index = 0;

        int start = Math.max(0, index - maxLength / 2);
        int end = Math.min(content.length(), start + maxLength);
        return content.substring(start, end) + (end < content.length() ? "..." : "");
    }

    @Override
    public void indexSite(String url) {
        List<SitesPageTable> pagesToIndex = pageRepository.findBySite_Url(url);

        for (SitesPageTable page : pagesToIndex) {
            String content = page.getContent();
            if (content == null || content.isBlank()) continue;

            Map<String, Integer> lemmas = LemmaEngine.getLemmas(content);

            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemmaStr = entry.getKey();
                int frequency = entry.getValue();

                Lemma lemma = lemmaRepository.findByLemma(lemmaStr)
                        .orElseGet(() -> {
                            Lemma newLemma = new Lemma();
                            newLemma.setLemma(lemmaStr);
                            newLemma.setFrequency(frequency);
                            return lemmaRepository.save(newLemma);
                        });

                SearchIndex index = new SearchIndex();
                index.setLemma(lemma);
                index.setPage(page);
                index.setRank((float) frequency);
                indexRepository.save(index);
            }
        }
    }
}
