package searchengine.model;

import javax.persistence.*;
import java.time.LocalDateTime;

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


}
