package searchengine.model;

import javax.persistence.*;

@Entity
@Table(name = "page", indexes = @javax.persistence.Index(name = "idx_path", columnList = "path"))
public class SitesPageTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(500)")
    private String path;

    private Integer code;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private SiteTable site;

    public SitesPageTable() {}

    public SitesPageTable(SiteTable site, String path, Integer code, String content) {
        this.site = site;
        this.path = path;
        this.code = code;
        this.content = content;
    }

    public SitesPageTable(String path, String content, SiteTable site) {
        this.path = path;
        this.content = content;
        this.site = site;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
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

    public String getSiteUrl() {
        return site != null ? site.getUrl() : null;
    }

    public SiteTable getSiteTable() {
        return site;
    }
}
