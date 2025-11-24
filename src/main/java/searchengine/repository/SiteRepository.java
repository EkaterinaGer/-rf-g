package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.SiteTable;

public interface SiteRepository extends JpaRepository<SiteTable, Long> {
    SiteTable findByUrl(String url);
}