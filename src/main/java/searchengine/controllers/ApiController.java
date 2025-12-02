package searchengine.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import searchengine.services.LemmaService;

import java.io.IOException;
import java.util.Map;

@RestController
public class ApiController {

    private final LemmaService lemmaService;

    public ApiController(LemmaService lemmaService) {
        this.lemmaService = lemmaService;
    }


    @GetMapping("/api/lemmas")
    public Map<String, Integer> getLemmas(@RequestParam String text) throws IOException {
        return lemmaService.getLemmas(text);
    }
}
