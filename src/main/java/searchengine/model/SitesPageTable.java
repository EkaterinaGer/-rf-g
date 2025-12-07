package searchengine.model;

import javax.persistence.*;

@Entity
@Table(name = "page")
public class SitesPageTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteTable site;

    public SitesPageTable() {}

    public SitesPageTable(String path, String content, SiteTable site) {
        this.path = path;
        this.content = content;
        this.site = site;
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SiteTable getSite() {
        return site;
    }

    public void setSite(SiteTable site) {
        this.site = site;
    }

    /**
     * Если нужен URL сайта
     */
    public String getSiteUrl() {
        return site != null ? site.getUrl() : null;
    }

    /**
     * Получить объект SiteTable
     */
    public SiteTable getSiteTable() {
        return site;
    }

    public void setSiteId(Long id) {

    }
}
