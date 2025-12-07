package searchengine.model;

import javax.persistence.*;

@Entity
@Table(name = "search_index")
public class SearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private SitesPageTable page;

    @ManyToOne
    @JoinColumn(name = "lemma_id", nullable = false)
    private Lemma lemma;

    private double rank;

    public SearchIndex() {}

    public SearchIndex(SitesPageTable page, Lemma lemma, double rank) {
        this.page = page;
        this.lemma = lemma;
        this.rank = rank;
    }

    public Long getId() { return id; }
    public SitesPageTable getPage() { return page; }
    public void setPage(SitesPageTable page) { this.page = page; }
    public Lemma getLemma() { return lemma; }
    public void setLemma(Lemma lemma) { this.lemma = lemma; }
    public double getRank() { return rank; }
    public void setRank(double rank) { this.rank = rank; }
}
