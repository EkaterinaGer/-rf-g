package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.exception.LemmaException;
import searchengine.services.LemmaService;

@Slf4j
@Service
@RequiredArgsConstructor
public class LemmaServiceImpl implements LemmaService {

    @Override
    public void processLemmas(String text) {
        log.info("Обработка лемм для текста длиной {}", text.length());
        try {

        } catch (Exception e) {
            log.error("Ошибка обработки лемм", e);
            throw new LemmaException("Ошибка при обработке лемм");
        }
    }
}
