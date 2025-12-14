package searchengine.model;

import javax.persistence.*;

@Entity
@Table(name = "search_index")
public class SearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private SitesPageTable page;

    @ManyToOne
    @JoinColumn(name = "lemma_id", nullable = false)
    private Lemma lemma;

    @Column(name = "rank")
    private Float rank;

    public SearchIndex() {}

    public SearchIndex(SitesPageTable page, Lemma lemma, Float rank) {
        this.page = page;
        this.lemma = lemma;
        this.rank = rank;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public SitesPageTable getPage() { return page; }
    public void setPage(SitesPageTable page) { this.page = page; }
    public Lemma getLemma() { return lemma; }
    public void setLemma(Lemma lemma) { this.lemma = lemma; }
    public Float getRank() { return rank; }
    public void setRank(Float rank) { this.rank = rank; }
}
