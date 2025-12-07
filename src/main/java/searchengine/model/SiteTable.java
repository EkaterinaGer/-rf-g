package searchengine.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "site")
public class SiteTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;

    @Enumerated(EnumType.STRING)
    private SiteStatusType status;

    private LocalDateTime statusTime;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<SitesPageTable> pages = new ArrayList<>();

    public SiteTable() {}

    public SiteTable(String name, String url, SiteStatusType status) {
        this.name = name;
        this.url = url;
        this.status = status;
        this.statusTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public SiteStatusType getStatus() { return status; }
    public void setStatus(SiteStatusType status) {
        this.status = status;
        this.statusTime = LocalDateTime.now(); // обновляем время при изменении статуса
    }
    public LocalDateTime getStatusTime() { return statusTime; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public List<SitesPageTable> getPages() { return pages; }
    public void setPages(List<SitesPageTable> pages) { this.pages = pages; }
}
