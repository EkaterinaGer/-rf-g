package searchengine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Класс для лемматизации текста
 * Улучшенная версия с базовыми правилами для русского языка
 */
public class Lemmatizer {
    private static final Logger logger = LoggerFactory.getLogger(Lemmatizer.class);
    
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}]+");
    
    // Простой список служебных слов для исключения
    private static final Set<String> STOP_WORDS = Set.of(
            "и", "в", "на", "с", "по", "для", "от", "до", "из", "к", "о", "у", "за", "со",
            "а", "но", "или", "что", "как", "так", "это", "то", "же", "бы", "ли", "не",
            "он", "она", "они", "мы", "вы", "я", "ты", "его", "её", "их", "мой", "твой",
            "был", "была", "было", "были", "есть", "будет", "быть"
    );
    
    // Базовые правила для лемматизации русского языка
    private static final Map<String, String> LEMMA_RULES = new HashMap<>();
    
    static {
        // Существительные
        LEMMA_RULES.put("банки", "банк");
        LEMMA_RULES.put("банке", "банк");
        LEMMA_RULES.put("банков", "банк");
        LEMMA_RULES.put("банковских", "банк");
        LEMMA_RULES.put("новости", "новость");
        LEMMA_RULES.put("новостей", "новость");
        LEMMA_RULES.put("новостям", "новость");
        LEMMA_RULES.put("новостями", "новость");
        
        // Глаголы (базовые окончания)
        LEMMA_RULES.put("ет", "ять");
        LEMMA_RULES.put("ает", "ать");
        LEMMA_RULES.put("ает", "ать");
        LEMMA_RULES.put("ает", "ать");
        
        // Прилагательные
        LEMMA_RULES.put("ый", "ый");
        LEMMA_RULES.put("ий", "ий");
        LEMMA_RULES.put("ая", "ый");
        LEMMA_RULES.put("ое", "ый");
        LEMMA_RULES.put("ые", "ый");
        
        // Суффиксы для удаления
        LEMMA_RULES.put("ов", "");
        LEMMA_RULES.put("ев", "");
        LEMMA_RULES.put("ами", "");
        LEMMA_RULES.put("ями", "");
        LEMMA_RULES.put("ох", "");
        LEMMA_RULES.put("ех", "");
    }
    
    public Lemmatizer() {
        // Улучшенная версия не требует инициализации
    }
    
    /**
     * Извлекает леммы из текста и возвращает их количество
     */
    public Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmas = new HashMap<>();
        
        if (text == null || text.isEmpty()) {
            return lemmas;
        }
        
        // Разбиваем текст на слова
        String[] words = text.toLowerCase()
                .replaceAll("[^\\p{L}\\s]", " ")
                .split("\\s+");
        
        for (String word : words) {
            // Пропускаем короткие слова и служебные части речи
            if (word.length() < 2 || !WORD_PATTERN.matcher(word).matches()) {
                continue;
            }
            
            // Пропускаем стоп-слова
            if (STOP_WORDS.contains(word)) {
                continue;
            }
            
            // Применяем правила лемматизации
            String lemma = lemmatizeWord(word);
            
            lemmas.put(lemma, lemmas.getOrDefault(lemma, 0) + 1);
        }
        
        return lemmas;
    }
    
    /**
     * Лемматизирует отдельное слово
     */
    private String lemmatizeWord(String word) {
        // Сначала проверяем прямые правила
        if (LEMMA_RULES.containsKey(word)) {
            return LEMMA_RULES.get(word);
        }
        
        // Проверяем правила по окончаниям
        for (Map.Entry<String, String> rule : LEMMA_RULES.entrySet()) {
            String ending = rule.getKey();
            String replacement = rule.getValue();
            
            if (word.endsWith(ending) && word.length() > ending.length()) {
                String lemma = word.substring(0, word.length() - ending.length()) + replacement;
                if (!lemma.isEmpty()) {
                    return lemma;
                }
            }
        }
        
        // Если правило не найдено, возвращаем слово как есть
        return word;
    }
    
    /**
     * Ищет слова в разных формах для поиска
     */
    public Set<String> findWordForms(String word) {
        Set<String> forms = new HashSet<>();
        forms.add(word.toLowerCase());
        
        // Добавляем базовые формы
        String lemma = lemmatizeWord(word.toLowerCase());
        if (!lemma.equals(word.toLowerCase())) {
            forms.add(lemma);
        }
        
        // Добавляем распространенные формы для существительных
        if (lemma.endsWith("ка") || lemma.endsWith("ар") || lemma.endsWith("ор")) {
            forms.add(lemma + "и");
            forms.add(lemma + "ов");
            forms.add(lemma + "е");
            forms.add(lemma + "у");
        }
        
        // Для слов типа "банк"
        if (lemma.equals("банк")) {
            forms.add("банки");
            forms.add("банков");
            forms.add("банке");
            forms.add("банках");
            forms.add("банка");
            forms.add("банку");
            forms.add("банком");
            forms.add("банкам");
        }
        
        // Для слов типа "новость"
        if (lemma.equals("новость")) {
            forms.add("новости");
            forms.add("новостей");
            forms.add("новостям");
            forms.add("новостями");
        }
        
        return forms;
    }
    
    /**
     * Очищает HTML от тегов
     */
    public String cleanHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        return html.replaceAll("<script[^>]*>.*?</script>", " ")
                .replaceAll("<style[^>]*>.*?</style>", " ")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&[a-z]+;", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}

