package searchengine.dto.responses;

import java.util.List;

public class ResultDto {

    private boolean result = true;
    private List<SearchResultItem> data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<SearchResultItem> getData() {
        return data;
    }

    public void setData(List<SearchResultItem> data) {
        this.data = data;
    }

    public static class SearchResultItem {
        private String site;
        private String title;
        private String uri;
        private String snippet;
        private double relevance;


    }
}
