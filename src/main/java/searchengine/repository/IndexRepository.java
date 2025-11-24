package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.SearchIndex;

import java.util.List;

public interface IndexRepository extends JpaRepository<SearchIndex, Long> {
    List<SearchIndex> findByLemmaId(Long lemmaId);
    List<SearchIndex> findByPageId(Long pageId);
}
