package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import searchengine.config.SitesList;
import searchengine.dto.responses.NotOkResponse;
import searchengine.dto.responses.OkResponse;
import searchengine.dto.statistics.statistics.StatisticsResponse;
import searchengine.model.SitePage;
import searchengine.services.ApiService;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final SearchService searchService;
    private final StatisticsService statisticsService;
    private final ApiService apiService;
    private final AtomicBoolean indexingProcessing = new AtomicBoolean(false);
    private final SitesList sitesList;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @GetMapping("/statistics")
    public StatisticsResponse statistics() throws IOException {
        return statisticsService.getStatistics();
    }

    @GetMapping("/startIndexing")
    public Object startIndexing() {
        if (indexingProcessing.get()) {
            return new NotOkResponse("Индексация уже запущена");
        } else {
            executor.submit(() -> {
                indexingProcessing.set(true);
                apiService.startIndexing(indexingProcessing);
            });
            return new OkResponse();
        }
    }

    @GetMapping("/stopIndexing")
    public Object stopIndexing() {
        if (!indexingProcessing.get()) {
            return new NotOkResponse("Индексация не запущена");
        } else {
            indexingProcessing.set(false);
            return new OkResponse();
        }
    }

    @PostMapping("/indexPage")
    public Object indexPage(@RequestParam String url) throws IOException {
        URL refUrl = new URL(url);
        SitePage sitePage = new SitePage();
        sitesList.getSites().stream()
                .filter(site -> refUrl.getHost().equals(site.getUrl().getHost()))
                .findFirst()
                .ifPresentOrElse(site -> {
                    sitePage.setName(site.getName());
                    sitePage.setUrl(site.getUrl().toString());
                }, () -> {
                    throw new IllegalArgumentException(
                            "Данная страница находится за пределами сайтов указанных в конфигурационном файле");
                });
        apiService.refreshPage(sitePage, refUrl);
        return new OkResponse();
    }
}
