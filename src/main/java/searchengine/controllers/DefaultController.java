package searchengine.controllers;

import searchengine.model.SearchResult;
import searchengine.services.SearchServiceNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class DefaultController {
    
    @Autowired
    private SearchServiceNew searchService;
    
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }
    
    @GetMapping("/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam String query, 
                                      @RequestParam(required = false) String site) {
        List<SearchResult> results = searchService.search(query, site);
        return Map.of(
                "result", true,
                "count", results.size(),
                "data", results
        );
    }
}
