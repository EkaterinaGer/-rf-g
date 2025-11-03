package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.exception.SearchException;
import searchengine.services.SearchService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    @Override
    public Object search(String query, String site, Integer offset, Integer limit) {
        log.info("Выполняется поиск: query='{}', site='{}', offset={}, limit={}", query, site, offset, limit);
        try {
            if (query == null || query.isBlank()) {
                throw new SearchException("Задан пустой поисковый запрос");
            }

            return new Object();
        } catch (SearchException e) {
            log.error("Ошибка поиска: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка поиска", e);
            throw new SearchException("Не удалось выполнить поиск");
        }
    }
}
