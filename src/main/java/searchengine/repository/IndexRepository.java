package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.SearchIndex;
import searchengine.model.SitesPageTable;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<SearchIndex, Integer> {
    List<SearchIndex> findByLemma(Lemma lemma);
    List<SearchIndex> findByPage(SitesPageTable page);
    List<SearchIndex> findByLemmaId(Integer lemmaId);
    List<SearchIndex> findByPageId(Integer pageId);
    List<SearchIndex> findByLemmaIdIn(List<Integer> lemmaIds);
    List<SearchIndex> findByLemmaIdInAndPage_Site_Url(List<Integer> lemmaIds, String siteUrl);
    SearchIndex findByPageIdAndLemmaId(Integer pageId, Integer lemmaId);
}
