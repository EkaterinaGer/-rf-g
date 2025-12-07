package searchengine.dto;

public class SearchDto {
    private String query;
    private int page = 1;
    private int size = 10;
    private String site; // фильтр по сайту (опционально)

    public SearchDto() {}

    public SearchDto(String query, int page, int size, String site) {
        this.query = query;
        this.page = page;
        this.size = size;
        this.site = site;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
}
