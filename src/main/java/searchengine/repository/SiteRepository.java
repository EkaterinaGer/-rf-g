package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.SiteTable;

import java.util.List;

public interface SiteRepository extends JpaRepository<SiteTable, Long> {

    SiteTable findByUrl(String url);

    // Найти все сайты по списку URL (удобно для удаления нескольких сайтов)
    List<SiteTable> findByUrlIn(List<String> urls);

}
