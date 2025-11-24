package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.SitesPageTable;

import java.util.List;

public interface PageRepository extends JpaRepository<SitesPageTable, Long> {
    List<SitesPageTable> findBySiteId(Long siteId);
}
