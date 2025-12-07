package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.Lemma;
import searchengine.model.SiteTable;

import java.util.List;
import java.util.Optional;

public interface LemmaRepository extends JpaRepository<Lemma, Long> {

    // Находит лемму по строке
    Optional<Lemma> findByLemma(String lemma);

    // Если нужно подсчитать количество лемм для сайта
    long countBySiteTable(SiteTable site);

    List<Lemma> findByLemmaIn(List<String> queryLemmas);
}
