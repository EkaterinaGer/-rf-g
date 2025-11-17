package searchengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(
        name = "search_index",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"page_id", "lemma_id"})}
)
@Data
@NoArgsConstructor
public class SearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "page_id", nullable = false)
    private Integer pageId;

    @Column(name = "lemma_id", nullable = false)
    private Integer lemmaId;

    @Column(nullable = false, name = "lemma_count")
    private Float lemmaCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", insertable = false, updatable = false, nullable = false)
    private SitesPageTable sitesPageTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", insertable = false, updatable = false, nullable = false)
    private Lemma lemma;

    @Column(nullable = false, name = "index_rank")
    private Float rank;
}