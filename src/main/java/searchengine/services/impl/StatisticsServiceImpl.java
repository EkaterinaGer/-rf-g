package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.StatisticsService;
import searchengine.model.SiteTable;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.ArrayList;
import java.util.List;

//@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesList sites;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;
    
    public StatisticsServiceImpl(SitesList sites, PageRepository pageRepository, 
                                  LemmaRepository lemmaRepository, SiteRepository siteRepository) {
        this.sites = sites;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.siteRepository = siteRepository;
    }

    @Override
    public StatisticsResponse getStatistics() {
        // Общая статистика
        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites() != null ? sites.getSites().size() : 0);
        total.setIndexing(false);
        total.setPages(0);
        total.setLemmas(0);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();

        if (sites.getSites() != null) {
            for (Site siteConfig : sites.getSites()) {
                SiteTable siteTable = siteRepository.findByUrl(siteConfig.getUrl() != null ? siteConfig.getUrl().toString() : "").orElse(null);
                if (siteTable == null) continue;

                DetailedStatisticsItem item = new DetailedStatisticsItem();
                item.setName(siteTable.getName());
                item.setUrl(siteTable.getUrl());
                item.setPages((int) pageRepository.countBySite(siteTable));
                // Подсчет лемм для сайта через индексы
                long lemmasCount = siteTable.getPages().stream()
                        .flatMap(p -> java.util.stream.Stream.empty())
                        .count();
                item.setLemmas(0); // Временно 0, так как нет прямого метода
            item.setStatus(siteTable.getStatus() != null ? siteTable.getStatus().name() : "UNKNOWN");
            item.setError(siteTable.getLastError() != null ? siteTable.getLastError() : "");


            total.setPages(total.getPages() + item.getPages());
            total.setLemmas(total.getLemmas() + item.getLemmas());

            if (siteTable.getStatus() == null || siteTable.getStatus().name().equals("INDEXING")) {
                total.setIndexing(true);
            }

                detailed.add(item);
            }
        }

        // Используем новую структуру StatisticsResponse
        StatisticsResponse.TotalStatistics totalStats = new StatisticsResponse.TotalStatistics(
                total.getSites(), total.getPages(), total.getLemmas(), total.isIndexing());
        
        List<StatisticsResponse.DetailedStatisticsItem> detailedItems = detailed.stream()
                .map(item -> {
                    StatisticsResponse.DetailedStatisticsItem newItem = new StatisticsResponse.DetailedStatisticsItem();
                    newItem.setName(item.getName());
                    newItem.setUrl(item.getUrl());
                    newItem.setPages(item.getPages());
                    newItem.setLemmas(item.getLemmas());
                    newItem.setStatus(item.getStatus());
                    newItem.setLastError(item.getError());
                    newItem.setStatusTime(item.getStatusTime());
                    return newItem;
                })
                .collect(java.util.stream.Collectors.toList());

        return new StatisticsResponse(totalStats, detailedItems);
    }
}
