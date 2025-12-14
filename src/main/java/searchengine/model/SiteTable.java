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
    private Integer id;

    @Column(columnDefinition = "VARCHAR(255)")
    private String name;
    
    @Column(columnDefinition = "VARCHAR(255)")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)")
    private SiteStatusType status;

    @Column(name = "status_time")
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<SitesPageTable> pages = new ArrayList<>();

    public SiteTable() {}

    public SiteTable(String url, String name) {
        this.url = url;
        this.name = name;
        this.status = SiteStatusType.INDEXING;
        this.statusTime = LocalDateTime.now();
    }

    public SiteTable(String name, String url, SiteStatusType status) {
        this.name = name;
        this.url = url;
        this.status = status;
        this.statusTime = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public SiteStatusType getStatus() { return status; }
    public void setStatus(SiteStatusType status) {
        this.status = status;
        this.statusTime = LocalDateTime.now();
    }
    public LocalDateTime getStatusTime() { return statusTime; }
    public void setStatusTime(LocalDateTime statusTime) { this.statusTime = statusTime; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public List<SitesPageTable> getPages() { return pages; }
    public void setPages(List<SitesPageTable> pages) { this.pages = pages; }
}
