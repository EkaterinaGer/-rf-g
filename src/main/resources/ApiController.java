package searchengine.controllers;

import org.springframework.web.bind.annotation.*;
import searchengine.dto.responses.ResultDto;
import searchengine.services.search.SearchService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearchService searchService;

    public ApiController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResultDto search(@RequestParam String query) {
        return searchService.clearCodeFromTag(query);
    }
}
