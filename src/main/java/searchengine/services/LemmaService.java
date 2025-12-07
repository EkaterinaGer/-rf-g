package searchengine.services;

import java.io.IOException;
import java.util.Map;

public interface LemmaService {

    /**
     * Возвращает карту лемм и их частот из текста.
     */
    Map<String, Integer> getLemmas(String text) throws IOException;

    /**
     * Более низкоуровневый метод для извлечения лемм из HTML или текста.
     */
    Map<String, Integer> getLemmasFromText(String html);
}
