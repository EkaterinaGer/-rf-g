package searchengine.dto.responses;

import java.util.List;

public class ResultDto {

    private boolean result;
    private List<SearchResultItem> data;


    public ResultDto(String path, String snippet, double relevance) {
        this.result = true;
    }

    public ResultDto(boolean result, List<SearchResultItem> data) {
        this.result = result;
        this.data = data;
    }

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

        public SearchResultItem() {
        }

        public SearchResultItem(String site, String title, String uri, String snippet, double relevance) {
            this.site = site;
            this.title = title;
            this.uri = uri;
            this.snippet = snippet;
            this.relevance = relevance;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public double getRelevance() {
            return relevance;
        }

        public void setRelevance(double relevance) {
            this.relevance = relevance;
        }
    }
}
