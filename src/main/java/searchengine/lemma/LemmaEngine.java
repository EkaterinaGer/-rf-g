package searchengine.lemma;

import org.apache.lucene.analysis.ru.RussianLightStemmer;

import java.util.HashMap;
import java.util.Map;

public class LemmaEngine {

    private static RussianLightStemmer stemmer;

    public LemmaEngine() {
        this.stemmer = new RussianLightStemmer();
    }


    public static Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmaCount = new HashMap<>();
        if (text == null || text.isBlank()) return lemmaCount;

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


    public Map<String, Integer> getLemmasFromText(String html) {
        if (html == null || html.isBlank()) return new HashMap<>();

        // Убираем HTML-теги, оставляем только текст
        String textOnly = html.replaceAll("<[^>]*>", " ");
        return getLemmas(textOnly);
    }
}
