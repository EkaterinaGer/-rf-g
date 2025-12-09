package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.SitesPageTable;
import searchengine.model.SiteTable;

import java.util.List;

public interface PageRepository extends JpaRepository<SitesPageTable, Long> {


    long countBySite(SiteTable site);


    List<SitesPageTable> findBySite_Url(String url);
}