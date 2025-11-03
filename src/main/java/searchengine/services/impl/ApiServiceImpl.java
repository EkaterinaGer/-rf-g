package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.exception.IndexingException;
import searchengine.model.SitePage;
import searchengine.services.ApiService;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

    @Override
    public void startIndexing(AtomicBoolean indexingProcessing) {
        log.info("Начало индексации сайтов");
        try {

        } catch (Exception e) {
            log.error("Ошибка индексации", e);
            throw new IndexingException(e.getMessage());
        } finally {
            indexingProcessing.set(false);
            log.info("Индексация завершена");
        }
    }

    @Override
    public void refreshPage(SitePage sitePage, URL url) {
        log.info("Обновление страницы: {}", url);
        try {

        } catch (Exception e) {
            log.error("Ошибка при обновлении страницы {}", url, e);
            throw new IndexingException("Ошибка при обновлении страницы");
        }
    }
}
