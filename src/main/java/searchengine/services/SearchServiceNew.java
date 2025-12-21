package searchengine.services;

import searchengine.model.*;
import searchengine.repository.*;
import searchengine.util.Lemmatizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceNew {
    private static final Logger logger = LoggerFactory.getLogger(SearchServiceNew.class);
    
    // Процент страниц, при превышении которого лемма исключается (80%)
    private static final double MAX_LEMMA_PERCENTAGE = 0.8;
    
    @Autowired
    private LemmaRepository lemmaRepository;
    
    @Autowired
    private IndexRepository indexRepository;
    
    @Autowired
    private PageRepository pageRepository;
    
    @Autowired
    private SiteRepository siteRepository;
    
    private final Lemmatizer lemmatizer = new Lemmatizer();
    
    public List<SearchResult> search(String query, String siteUrl) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        logger.info("Поиск по запросу: '{}', сайт: {}", query, siteUrl);
        
        // 1. Разбиваем запрос на слова и формируем список уникальных лемм
        Map<String, Integer> queryLemmasMap = lemmatizer.getLemmas(query);
        Set<String> queryLemmaStrings = queryLemmasMap.keySet();
        
        logger.info("Извлечено лемм из запроса: {}", queryLemmaStrings);
        
        if (queryLemmaStrings.isEmpty()) {
            logger.info("Не найдено лемм в запросе");
            return Collections.emptyList();
        }
        
        // 2. Расширяем запрос формами слов для лучшего поиска
        Set<String> expandedQueryTerms = new HashSet<>();
        Set<String> queryWords = extractWordsFromQuery(query);
        
        // Добавляем все формы для каждого слова
        for (String word : queryWords) {
            expandedQueryTerms.addAll(lemmatizer.findWordForms(word));
        }
        
        // Добавляем леммы
        expandedQueryTerms.addAll(queryLemmaStrings);
        
        logger.info("Расширенный набор терминов для поиска: {}", expandedQueryTerms);
        
        // Получаем общее количество страниц для расчета процента
        long totalPages = siteUrl != null 
            ? pageRepository.findAll().stream()
                .filter(p -> p.getSite().getUrl().equals(siteUrl))
                .count()
            : pageRepository.count();
        
        if (totalPages == 0) {
            return Collections.emptyList();
        }
        
        // 3. Находим леммы в БД и исключаем те, что встречаются на слишком большом количестве страниц
        // Если страниц мало (меньше 10), не применяем фильтр по проценту
        boolean applyPercentageFilter = totalPages >= 10;
        
        List<Lemma> foundLemmas = new ArrayList<>();
        for (String lemmaText : expandedQueryTerms) {
            Optional<Lemma> lemmaOpt = lemmaRepository.findByLemma(lemmaText);
            if (lemmaOpt.isPresent()) {
                Lemma lemma = lemmaOpt.get();
                logger.info("Найдена лемма '{}' с frequency={}", lemmaText, lemma.getFrequency());
                
                if (applyPercentageFilter) {
                    // Исключаем леммы, которые встречаются на более чем MAX_LEMMA_PERCENTAGE страниц
                    double lemmaPercentage = (double) lemma.getFrequency() / totalPages;
                    if (lemmaPercentage <= MAX_LEMMA_PERCENTAGE) {
                        foundLemmas.add(lemma);
                        logger.info("Лемма '{}' добавлена (встречается на {:.2f}% страниц)", 
                            lemmaText, lemmaPercentage * 100);
                    } else {
                        logger.info("Лемма '{}' исключена (встречается на {:.2f}% страниц)", 
                            lemmaText, lemmaPercentage * 100);
                    }
                } else {
                    // Если страниц мало, не фильтруем по проценту
                    foundLemmas.add(lemma);
                    logger.info("Лемма '{}' добавлена (фильтр по проценту отключен)", lemmaText);
                }
            } else {
                logger.info("Лемма '{}' не найдена в базе данных", lemmaText);
            }
        }
        
        logger.info("Найдено подходящих лемм после фильтрации: {}", foundLemmas.size());
        
        if (foundLemmas.isEmpty()) {
            logger.warn("Не найдено подходящих лемм после фильтрации. Запрос: '{}', извлеченные леммы: {}", query, queryLemmaStrings);
            // Пробуем найти хотя бы одну лемму из исходного запроса
            for (String lemmaText : queryLemmaStrings) {
                Optional<Lemma> lemmaOpt = lemmaRepository.findByLemma(lemmaText);
                if (lemmaOpt.isPresent()) {
                    foundLemmas.add(lemmaOpt.get());
                    logger.info("Добавлена лемма '{}' без фильтрации", lemmaText);
                    break; 
                }
            }
            // Если все еще ничего не найдено, пробуем формы слов
            if (foundLemmas.isEmpty()) {
                for (String word : queryWords) {
                    var wordForms = lemmatizer.findWordForms(word);
                    for (String form : wordForms) {
                        Optional<Lemma> lemmaOpt = lemmaRepository.findByLemma(form);
                        if (lemmaOpt.isPresent()) {
                            foundLemmas.add(lemmaOpt.get());
                            logger.info("Добавлена форма слова '{}' без фильтрации", form);
                            break;
                        }
                    }
                    if (!foundLemmas.isEmpty()) break;
                }
            }
            if (foundLemmas.isEmpty()) {
                return Collections.emptyList();
            }
        }
        
        // 4. Сортируем леммы по возрастанию частоты (от самых редких к самым частым)
        foundLemmas.sort(Comparator.comparingInt(Lemma::getFrequency));
        
        logger.info("Найдено {} лемм после фильтрации, отсортировано по частоте", foundLemmas.size());
        
        // 5. Находим страницы для самой редкой леммы
        Lemma firstLemma = foundLemmas.get(0);
        Set<SitesPageTable> candidatePages = indexRepository.findByLemma(firstLemma).stream()
            .map(SearchIndex::getPage)
            .filter(page -> siteUrl == null || page.getSite().getUrl().equals(siteUrl))
            .collect(Collectors.toSet());
        
        if (candidatePages.isEmpty()) {
            logger.info("Не найдено страниц для первой леммы");
            return Collections.emptyList();
        }
        
        // 6. Для каждой следующей леммы находим страницы, которые есть и в предыдущем списке
        for (int i = 1; i < foundLemmas.size(); i++) {
            Lemma lemma = foundLemmas.get(i);
            Set<SitesPageTable> pagesForLemma = indexRepository.findByLemma(lemma).stream()
                .map(SearchIndex::getPage)
                .filter(page -> siteUrl == null || page.getSite().getUrl().equals(siteUrl))
                .collect(Collectors.toSet());
            
            // Пересечение: оставляем только страницы, которые есть в обоих множествах
            candidatePages.retainAll(pagesForLemma);
            
            // 7. Если страниц не осталось, но это не последняя лемма - продолжаем с оставшимися
            if (candidatePages.isEmpty() && i < foundLemmas.size() - 1) {
                logger.info("Не осталось страниц после обработки леммы {}, продолжаем поиск", lemma.getLemma());
                // Продолжаем с оставшимися леммами, но используем страницы из предыдущей леммы
                Set<SitesPageTable> prevPages = indexRepository.findByLemma(foundLemmas.get(i-1)).stream()
                    .map(SearchIndex::getPage)
                    .filter(page -> siteUrl == null || page.getSite().getUrl().equals(siteUrl))
                    .collect(Collectors.toSet());
                candidatePages = prevPages;
            } else if (candidatePages.isEmpty()) {
                logger.info("Не осталось страниц после обработки всех лемм");
                return Collections.emptyList();
            }
        }
        
        logger.debug("Найдено {} страниц-кандидатов", candidatePages.size());
        
        // 8. Рассчитываем абсолютную релевантность для каждой страницы
        Map<SitesPageTable, Double> absoluteRelevance = new HashMap<>();
        
        for (SitesPageTable page : candidatePages) {
            double absRelevance = 0.0;
            for (Lemma lemma : foundLemmas) {
                Optional<SearchIndex> indexOpt = indexRepository.findByLemma(lemma).stream()
                    .filter(idx -> idx.getPage().equals(page))
                    .findFirst();
                if (indexOpt.isPresent()) {
                    absRelevance += indexOpt.get().getRank();
                }
            }
            absoluteRelevance.put(page, absRelevance);
        }
        
        // Находим максимальную абсолютную релевантность
        double maxAbsoluteRelevance = absoluteRelevance.values().stream()
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(1.0);
        
        // 9. Рассчитываем относительную релевантность и создаем результаты
        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<SitesPageTable, Double> entry : absoluteRelevance.entrySet()) {
            SitesPageTable page = entry.getKey();
            double absRel = entry.getValue();
            // Относительная релевантность = абсолютная / максимальная абсолютная
            double relativeRelevance = maxAbsoluteRelevance > 0 
                ? absRel / maxAbsoluteRelevance 
                : 0.0;
            
            String uri = page.getSite().getUrl() + page.getPath();
            String title = extractTitle(page.getContent());
            String snippet = createSnippet(page.getContent(), query, expandedQueryTerms);
            
            results.add(new SearchResult(uri, title, snippet, relativeRelevance));
        }
        
        // 10. Сортируем по убыванию относительной релевантности
        results.sort((a, b) -> Double.compare(b.getRelevance(), a.getRelevance()));
        
        logger.info("Найдено {} результатов поиска", results.size());
        
        return results;
    }
    
    /**
     * Извлекает заголовок из HTML
     */
    private String extractTitle(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        try {
            Document doc = Jsoup.parse(html);
            Element titleElement = doc.selectFirst("title");
            if (titleElement != null) {
                return titleElement.text().trim();
            }
            
            // Если нет title, пробуем h1
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) {
                return h1.text().trim();
            }
        } catch (Exception e) {
            logger.warn("Ошибка при извлечении заголовка: {}", e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Создает сниппет текста с выделением найденных слов
     */
    private String createSnippet(String html, String query, Set<String> queryLemmas) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        try {
            Document doc = Jsoup.parse(html);
            
            // Удаляем script и style
            doc.select("script, style").remove();
            
            // Получаем очищенный текст
            String cleanText = doc.body() != null ? doc.body().text() : doc.text();
            
            if (cleanText == null || cleanText.isEmpty()) {
                return "";
            }
            
            // Получаем все слова из оригинального запроса для выделения
            Set<String> queryWords = extractWordsFromQuery(query);
            
            // Расширяем поиск: добавляем все формы слов из запроса
            Set<String> searchTerms = new HashSet<>();
            searchTerms.addAll(queryWords);
            
            // Добавляем все формы каждого слова из запроса
            for (String word : queryWords) {
                searchTerms.addAll(lemmatizer.findWordForms(word));
            }
            
            // Добавляем леммы для дополнительного поиска
            searchTerms.addAll(queryLemmas);
            
            logger.info("Поиск в сниппете по терминам: {}", searchTerms);
            
            // Ищем лучшее место для сниппета в очищенном тексте
            String lowerText = cleanText.toLowerCase();
            int snippetStart = -1;
            int snippetLength = 300;
            int bestPos = -1;
            String bestTerm = "";
            String foundWord = ""; // Сохраняем найденное слово для выделения
            
            // Сначала ищем точные вхождения слов из запроса
            for (String word : queryWords) {
                int pos = lowerText.indexOf(word.toLowerCase());
                if (pos != -1) {
                    bestPos = pos;
                    bestTerm = word;
                    foundWord = word;
                    logger.info("Найдено точное вхождение слова '{}' на позиции {}", word, pos);
                    break;
                }
            }
            
            // Если не нашли точное вхождение, ищем любые формы слов
            if (bestPos == -1) {
                for (String term : searchTerms) {
                    int pos = lowerText.indexOf(term.toLowerCase());
                    if (pos != -1) {
                        bestPos = pos;
                        bestTerm = term;
                        foundWord = term;
                        logger.info("Найдено вхождение термина '{}' на позиции {}", term, pos);
                        break;
                    }
                }
            }
            
            // Если все еще не нашли, ищем частичное вхождение (первые 4-5 букв)
            if (bestPos == -1) {
                for (String word : queryWords) {
                    if (word.length() >= 4) {
                        // Ищем слова, начинающиеся с первых 4 букв запроса
                        String prefix = word.substring(0, Math.min(5, word.length())).toLowerCase();
                        String[] words = lowerText.split("\\s+");
                        int currentPos = 0;
                        for (String textWord : words) {
                            if (textWord.startsWith(prefix)) {
                                // Находим позицию этого слова в тексте
                                bestPos = lowerText.indexOf(textWord, currentPos);
                                if (bestPos != -1) {
                                    bestTerm = word;
                                    // Извлекаем найденное слово из оригинального текста
                                    int wordStart = bestPos;
                                    int wordEnd = bestPos + textWord.length();
                                    if (wordEnd <= cleanText.length()) {
                                        foundWord = cleanText.substring(wordStart, wordEnd);
                                        logger.info("Найдено частичное вхождение '{}' для слова '{}' на позиции {}", foundWord, word, bestPos);
                                        break;
                                    }
                                }
                            }
                            currentPos += textWord.length() + 1;
                        }
                        if (bestPos != -1) break;
                    }
                }
            }
            
            // Устанавливаем позицию сниппета
            if (bestPos != -1) {
                snippetStart = Math.max(0, bestPos - 100);
                logger.info("Сниппет будет создан вокруг позиции {} для слова '{}'", snippetStart, bestTerm);
            } else {
                snippetStart = 0;
                logger.info("Слово не найдено, используем начало документа");
            }
            
            // Берем фрагмент текста
            int end = Math.min(cleanText.length(), snippetStart + snippetLength);
            String snippet = cleanText.substring(snippetStart, end).trim();
            
            // Выделяем найденные слова тегами <b>
            // Создаем паттерны для всех форм слов из запроса
            Set<String> wordsToHighlight = new HashSet<>();
            wordsToHighlight.addAll(queryWords);
            
            // Добавляем формы слов для более точного поиска
            for (String word : queryWords) {
                wordsToHighlight.addAll(lemmatizer.findWordForms(word));
            }
            
            // Если слово нашлось частично, добавляем его в список для выделения
            if (!foundWord.isEmpty() && !foundWord.equals(bestTerm)) {
                wordsToHighlight.add(foundWord);
            }
            
            logger.info("Слова для выделения: {}", wordsToHighlight);
            
            // Выделяем каждое слово в сниппете
            for (String word : wordsToHighlight) {
                if (word.length() < 2) continue;
                
                try {
                    // Простой поиск без regex - ищем слово (регистронезависимо)
                    String lowerSnippet = snippet.toLowerCase();
                    String lowerWord = word.toLowerCase();
                    int index = 0;
                    
                    while ((index = lowerSnippet.indexOf(lowerWord, index)) != -1) {
                        // Проверяем границы слова
                        boolean isWordStart = (index == 0 || !Character.isLetter(snippet.charAt(index - 1)));
                        boolean isWordEnd = (index + word.length() >= snippet.length() || 
                                            !Character.isLetter(snippet.charAt(index + word.length())));
                        
                        if (isWordStart && isWordEnd) {
                            // Извлекаем оригинальное слово (с учетом регистра)
                            String originalWord = snippet.substring(index, index + word.length());
                            
                            // Заменяем только если еще не в тегах
                            if (index < 3 || !snippet.substring(index - 3, index).equals("<b>")) {
                                String before = snippet.substring(0, index);
                                String after = snippet.substring(index + word.length());
                                snippet = before + "<b>" + originalWord + "</b>" + after;
                                lowerSnippet = snippet.toLowerCase();
                                index += 7; // Длина "<b>" + слово + "</b>" = 7 + length
                            } else {
                                index += word.length();
                            }
                        } else {
                            index += word.length();
                        }
                    }
                    
                } catch (Exception e) {
                    logger.warn("Ошибка при выделении слова '{}': {}", word, e.getMessage());
                }
            }
            
            // Добавляем многоточие, если текст обрезан
            if (snippetStart > 0) {
                snippet = "..." + snippet;
            }
            if (end < cleanText.length()) {
                snippet = snippet + "...";
            }
            
            return snippet.trim();
            
        } catch (Exception e) {
            logger.warn("Ошибка при создании сниппета: {}", e.getMessage());
            // Fallback: просто очищаем HTML
            Lemmatizer lemmatizer = new Lemmatizer();
            String text = lemmatizer.cleanHtml(html);
            if (text.length() > 300) {
                text = text.substring(0, 300) + "...";
            }
            return text;
        }
    }
    
    /**
     * Извлекает отдельные слова из запроса (без лемматизации)
     */
    private Set<String> extractWordsFromQuery(String query) {
        Set<String> words = new HashSet<>();
        
        if (query == null || query.isEmpty()) {
            return words;
        }
        
        // Разбиваем запрос на слова, удаляем пунктуацию
        String[] queryWords = query.toLowerCase()
                .replaceAll("[^\\p{L}\\s]", " ")
                .split("\\s+");
        
        for (String word : queryWords) {
            word = word.trim();
            if (word.length() >= 2) {
                words.add(word);
            }
        }
        
        return words;
    }
}

