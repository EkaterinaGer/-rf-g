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

}
