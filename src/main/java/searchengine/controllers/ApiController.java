package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.responses.ResultDto;
import searchengine.search.SearchStarter;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearchStarter searchStarter;

    public ApiController(SearchStarter searchStarter) {
        this.searchStarter = searchStarter;
    }

    // ================== ПОИСК ==================
    @GetMapping("/search")
    public List<ResultDto> search(
            @RequestParam("query") String query,
            @RequestParam(value = "site", required = false) String site,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        if (site != null && !site.isBlank()) {
            return searchStarter.getSearchFromOneSite(query, site, page, size);
        } else {
            return searchStarter.getFullSearch(query, page, size);
        }
    }


    @PostMapping("/index")
    public ResponseEntity<String> startIndexing(@RequestParam("url") String url) {
        boolean started = searchStarter.startIndexing(url);
        if (started) {
            return ResponseEntity.ok("Индексация сайта началась: " + url);
        } else {
            return ResponseEntity.badRequest().body("Невозможно начать индексацию: " + url);
        }
    }


    @PostMapping("/stop")
    public ResponseEntity<String> stopIndexing() {
        boolean stopped = searchStarter.stopIndexing();
        if (stopped) {
            return ResponseEntity.ok("Индексация остановлена");
        } else {
            return ResponseEntity.badRequest().body("Индексация не запущена");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok(searchStarter.getStatus());
    }

    @GetMapping("/sites")
    public ResponseEntity<List<String>> getSites() {
        List<String> sites = searchStarter.getSites();
        return ResponseEntity.ok(sites);
    }
}
