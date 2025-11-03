package searchengine.services;

import searchengine.dto.statistics.StatisticsResponse;

import java.net.MalformedURLException;

public interface StatisticsService {
    searchengine.dto.statistics.statistics.StatisticsResponse getStatistics() throws MalformedURLException;
}