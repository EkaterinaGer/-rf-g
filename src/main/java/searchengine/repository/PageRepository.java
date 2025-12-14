package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.SitesPageTable;
import searchengine.model.SiteTable;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<SitesPageTable, Integer> {
    Optional<SitesPageTable> findBySiteAndPath(SiteTable site, String path);
    boolean existsBySiteAndPath(SiteTable site, String path);
    long countBySite(SiteTable site);
    List<SitesPageTable> findBySite_Url(String url);
}