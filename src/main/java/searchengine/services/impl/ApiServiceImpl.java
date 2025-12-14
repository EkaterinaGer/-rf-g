package searchengine.services.impl;

import lombok.RequiredArgsConstructor;

import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.config.Connection;
import searchengine.model.SitesPageTable;
import searchengine.model.SiteTable;
import searchengine.model.SiteStatusType;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.ApiService;
import searchengine.services.IndexService;
import searchengine.services.LemmaService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

//@Service
public class ApiServiceImpl implements ApiService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final SitesList indexingSites;
    private final Connection connection;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    
    public ApiServiceImpl(SiteRepository siteRepository, PageRepository pageRepository,
                         SitesList indexingSites, Connection connection,
                         LemmaService lemmaService, IndexService indexService) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.indexingSites = indexingSites;
        this.connection = connection;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
    }

    private AtomicBoolean indexingProcessing;
    private final Logger logger = Logger.getLogger(ApiServiceImpl.class.getName());

    @Override
    public void startIndexing(AtomicBoolean indexingProcessing) {
        this.indexingProcessing = indexingProcessing;
        try {
            deleteDataInDB();
            addSitesToDB();
            indexAllSites();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Индексация была прервана");
        } catch (Exception e) {
            logger.severe("Ошибка при индексации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stopIndexing() {

    }

    @Override
    public List<SiteTable> getAllSitesStatus() {
        return List.of();
    }

    @Override
    public boolean isIndexing() {
        return false;
    }

    private void deleteDataInDB() {
        List<String> siteUrls = indexingSites.getSites() != null ? 
                indexingSites.getSites().stream()
                        .map(site -> site.getUrl() != null ? site.getUrl().toString() : "")
                        .filter(url -> !url.isEmpty())
                        .toList() : 
                List.of();
        siteRepository.deleteAll(siteRepository.findByUrlIn(siteUrls));
    }

    private void addSitesToDB() {
        if (indexingSites.getSites() != null) {
            for (Site siteApp : indexingSites.getSites()) {
                SiteTable siteTable = new SiteTable();
                siteTable.setStatus(SiteStatusType.INDEXING);
                siteTable.setName(siteApp.getName() != null ? siteApp.getName() : "");
                siteTable.setUrl(siteApp.getUrl() != null ? siteApp.getUrl().toString() : "");
                siteRepository.save(siteTable);
            }
        }
    }

    private void indexAllSites() throws InterruptedException {
        List<SiteTable> sitesFromDB = siteRepository.findAll();
        ForkJoinPool pool = new ForkJoinPool(sitesFromDB.size());

        List<Thread> threads = new ArrayList<>();
        for (SiteTable siteTable : sitesFromDB) {
            Runnable indexSite = () -> {
                ConcurrentHashMap<String, SitesPageTable> resultPages = new ConcurrentHashMap<>();
                try {
                    logger.info("Запущена индексация: " + siteTable.getUrl());
                    pool.invoke(new SiteCrawler(siteRepository, pageRepository, siteTable,
                            "", resultPages, connection, indexingProcessing, lemmaService, indexService));

                    if (!indexingProcessing.get()) {
                        siteTable.setStatus(SiteStatusType.FAILED);
                        siteTable.setLastError("Индексация остановлена пользователем");
                    } else {
                        siteTable.setStatus(SiteStatusType.INDEXED);
                        logger.info("Проиндексирован сайт: " + siteTable.getName());
                    }
                    siteRepository.save(siteTable);
                } catch (Exception ex) {
                    siteTable.setStatus(SiteStatusType.FAILED);
                    siteTable.setLastError(ex.getMessage());
                    siteRepository.save(siteTable);
                    logger.severe("Ошибка при индексации " + siteTable.getUrl() + ": " + ex.getMessage());
                }
            };
            Thread thread = new Thread(indexSite);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        indexingProcessing.set(false);
        pool.shutdown();
    }
}
