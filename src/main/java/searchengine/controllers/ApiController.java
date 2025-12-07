package searchengine.controllers;

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
}
