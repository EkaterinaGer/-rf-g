package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.statistics.StatisticsResponse;
import searchengine.exception.StatisticsException;
import searchengine.services.StatisticsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public StatisticsResponse getStatistics() {
        log.info("Получение статистики");
        try {

            return new StatisticsResponse();
        } catch (Exception e) {
            log.error("Ошибка получения статистики", e);
            throw new StatisticsException("Ошибка при получении статистики");
        }
    }
}
