package searchengine.dto.statistics;

import java.util.List;

public class StatisticsResponse {

    private boolean result = true;
    private StatisticsData statistics;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public StatisticsData getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsData statistics) {
        this.statistics = statistics;
    }
}
