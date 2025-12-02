package searchengine.services.impl;

import org.springframework.stereotype.Service;
import searchengine.lemma.LemmaEngine;
import searchengine.services.LemmaService;

import java.io.IOException;
import java.util.Map;

@Service
public class LemmaServiceImpl implements LemmaService {

    private final LemmaEngine lemmaEngine;

    public LemmaServiceImpl() {
        this.lemmaEngine = new LemmaEngine();
    }

    @Override
    public Map<String, Integer> getLemmas(String text) throws IOException {
        return lemmaEngine.getLemmas(text);
    }
}
