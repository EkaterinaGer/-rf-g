package searchengine.lemma;

import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.util.*;

public class LemmaEngine {

    private RussianLuceneMorphology lucene;

    public LemmaEngine() throws IOException {
        lucene = new RussianLuceneMorphology();
    }

    public Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmaCount = new HashMap<>();
        String[] words = text.toLowerCase().replaceAll("[^а-яё\\s]", "").split("\\s+");
        for (String word : words) {
            if (word.isEmpty()) continue;
            List<String> normalForms = lucene.getNormalForms(word);
            for (String lemma : normalForms) {
                lemmaCount.put(lemma, lemmaCount.getOrDefault(lemma, 0) + 1);
            }
        }
        return lemmaCount;
    }
}
