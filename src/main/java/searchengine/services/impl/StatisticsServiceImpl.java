package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesList sites;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;

    @Override
    public StatisticsResponse getStatistics() {
        // Общая статистика
        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(false);
        total.setPages(0);
        total.setLemmas(0);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();

        for (Site siteConfig : sites.getSites()) {
            SiteTable siteTable = siteRepository.findByUrl(String.valueOf(siteConfig.getUrl()));
            if (siteTable == null) continue;

            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(siteTable.getName());
            item.setUrl(siteTable.getUrl());
            item.setPages((int) pageRepository.countBySite(siteTable));
            item.setLemmas((int) lemmaRepository.countBySiteTable(siteTable));
            item.setStatus(siteTable.getStatus() != null ? siteTable.getStatus().name() : "UNKNOWN");
            item.setError(siteTable.getLastError() != null ? siteTable.getLastError() : "");


            total.setPages(total.getPages() + item.getPages());
            total.setLemmas(total.getLemmas() + item.getLemmas());

            if (siteTable.getStatus() == null || siteTable.getStatus().name().equals("INDEXING")) {
                total.setIndexing(true);
            }

            detailed.add(item);
        }

        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);

        StatisticsResponse response = new StatisticsResponse();
        response.setStatistics(data);
        response.setResult(true);

        return response;
    }
}
