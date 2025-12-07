package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.SitesPageTable;
import searchengine.model.SiteTable;

public interface PageRepository extends JpaRepository<SitesPageTable, Long> {
    long countBySite(SiteTable site);
}
