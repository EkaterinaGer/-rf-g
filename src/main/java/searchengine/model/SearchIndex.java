package searchengine.model;

import javax.persistence.*;

@Entity
@Table(name = "search_index")
public class SearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private SitesPageTable page;

    @ManyToOne
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    private double rank;

}
