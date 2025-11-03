package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.exception.IndexingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageFinder {

    public void findPages(String siteUrl) {
        log.info("Поиск страниц на сайте {}", siteUrl);
        try {

        } catch (Exception e) {
            log.error("Ошибка поиска страниц на сайте {}", siteUrl, e);
            throw new IndexingException("Ошибка поиска страниц на сайте");
        }
    }
}
