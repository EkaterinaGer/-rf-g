package searchengine.dto.statistics;

public class DetailedStatisticsItem {

    private String name;
    private String url;
    private int pages;
    private int lemmas;
    private String status;
    private String error;
    private long statusTime;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public int getLemmas() { return lemmas; }
    public void setLemmas(int lemmas) { this.lemmas = lemmas; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public long getStatusTime() { return statusTime; }
    public void setStatusTime(long statusTime) { this.statusTime = statusTime; }
}
