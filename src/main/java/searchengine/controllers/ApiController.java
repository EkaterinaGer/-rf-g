package searchengine.controllers;

import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.SearchResult;
import searchengine.services.CrawlingService;
import searchengine.services.IndexingService;
import searchengine.services.SearchServiceNew;
import searchengine.services.StatisticsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final CrawlingService crawlingService;
    private final IndexingService indexingService;
    private final SearchServiceNew searchService;
    private final StatisticsService statisticsService;

    public ApiController(CrawlingService crawlingService, IndexingService indexingService,
                         SearchServiceNew searchService, StatisticsService statisticsService) {
        this.crawlingService = crawlingService;
        this.indexingService = indexingService;
        this.searchService = searchService;
        this.statisticsService = statisticsService;
    }

    /**
     * Запуск индексации (GET/POST)
     */
    @PostMapping("/startIndexing")
    public Map<String, Object> startIndexingPost(@RequestParam(required = false) String url) {
        if (url != null && !url.isBlank()) {
            crawlingService.startIndexing(url);
        } else {
            crawlingService.startIndexing();
        }
        return Map.of("result", true);
    }

    @GetMapping("/startIndexing")
    public Map<String, Object> startIndexingGet(@RequestParam(required = false) String url) {
        if (url != null && !url.isBlank()) {
            crawlingService.startIndexing(url);
        } else {
            crawlingService.startIndexing();
        }
        return Map.of("result", true);
    }

    /**
     * Остановка индексации (GET/POST)
     */
    @PostMapping("/stopIndexing")
    public Map<String, Object> stopIndexingPost() {
        crawlingService.stopIndexing();
        return Map.of("result", true);
    }

    @GetMapping("/stopIndexing")
    public Map<String, Object> stopIndexingGet() {
        crawlingService.stopIndexing();
        return Map.of("result", true);
    }

    /**
     * Индексация отдельной страницы
     */
    @PostMapping("/indexPage")
    public Map<String, Object> indexPage(@RequestParam String url) {
        indexingService.indexPage(url);
        return Map.of("result", true);
    }

    /**
     * Поиск
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String query,
                                      @RequestParam(required = false) String site) {
        List<SearchResult> results = searchService.search(query, site);
        return Map.of(
                "result", true,
                "count", results.size(),
                "data", results
        );
    }

    /**
     * Статистика
     */
    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        StatisticsResponse stats = statisticsService.getStatistics();
        return Map.of("result", true, "statistics", stats);
    }
    
}
