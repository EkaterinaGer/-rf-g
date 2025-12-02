package searchengine.lemma;

import org.apache.lucene.analysis.ru.RussianLightStemmer;

import java.util.HashMap;
import java.util.Map;


public class LemmaEngine {

    private final RussianLightStemmer stemmer;

    public LemmaEngine() {
        this.stemmer = new RussianLightStemmer();
    }


    public Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmaCount = new HashMap<>();
        if (text == null || text.isBlank()) {
            return lemmaCount;
        }

        String normalized = text.toLowerCase().replaceAll("[^а-яё\\s]", " ");
        String[] words = normalized.split("\\s+");
        for (String word : words) {
            if (word.isEmpty()) continue;
            char[] chars = word.toCharArray();
            int len = stemmer.stem(chars, chars.length);
            String lemma = new String(chars, 0, len);
            lemmaCount.put(lemma, lemmaCount.getOrDefault(lemma, 0) + 1);
        }
        return lemmaCount;
    }
}
