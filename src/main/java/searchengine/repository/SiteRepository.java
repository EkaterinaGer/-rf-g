package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteTable;
import searchengine.model.SiteStatusType;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<SiteTable, Integer> {
    Optional<SiteTable> findByUrl(String url);
    List<SiteTable> findByStatus(SiteStatusType status);
    List<SiteTable> findByUrlIn(List<String> urls);
}
