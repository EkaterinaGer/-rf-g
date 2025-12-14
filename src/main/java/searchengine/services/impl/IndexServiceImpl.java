package searchengine.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import searchengine.model.Lemma;
import searchengine.model.SearchIndex;
import searchengine.model.SitesPageTable;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.services.IndexService;
import searchengine.services.LemmaService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class IndexServiceImpl implements IndexService {

    private final LemmaService lemmaService;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    
    public IndexServiceImpl(LemmaService lemmaService, LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.lemmaService = lemmaService;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    @Override
    public void indexHtml(String html, SitesPageTable indexingPage) {
        long start = System.currentTimeMillis();
        Map<String, Integer> lemmas = lemmaService.getLemmasFromText(html);
        lemmas.entrySet().parallelStream().forEach(entry -> {
            try {
                saveLemma(entry.getKey(), entry.getValue(), indexingPage);
            } catch (Exception e) {
                // Ошибка при сохранении леммы
                System.err.println("Ошибка при сохранении леммы: " + entry.getKey() + " - " + e.getMessage());
            }
        });
        System.out.println("Индексация страницы завершена за " + (System.currentTimeMillis() - start) + "ms, lemmas: " + lemmas.size());
    }

    @Transactional
    public void saveLemma(String key, Integer value, SitesPageTable indexingPage) {
        Optional<Lemma> optionalLemma = lemmaRepository.findByLemma(key);
        if (optionalLemma.isPresent()) {
            Lemma existLemmaInDB = optionalLemma.get();
            existLemmaInDB.setFrequency(existLemmaInDB.getFrequency() + value);
            lemmaRepository.saveAndFlush(existLemmaInDB);
            createIndex(indexingPage, existLemmaInDB, value);
        } else {
            try {
                Lemma lemma = new Lemma();
                lemma.setLemma(key);
                lemma.setFrequency(value);
                lemmaRepository.saveAndFlush(lemma);
                createIndex(indexingPage, lemma, value);
            } catch (DataIntegrityViolationException e) {
                saveLemma(key, value, indexingPage);
            }
        }
    }

    private void createIndex(SitesPageTable indexingPage, Lemma lemmaInDB, Integer count) {
        SearchIndex searchIndexExists = indexRepository.findByPageIdAndLemmaId(indexingPage.getId(), lemmaInDB.getId());
        if (searchIndexExists != null) {
            searchIndexExists.setRank(searchIndexExists.getRank() + count.floatValue());
            indexRepository.save(searchIndexExists);
        } else {
            SearchIndex index = new SearchIndex();
            index.setLemma(lemmaInDB);
            index.setPage(indexingPage);
            index.setRank(count.floatValue());
            indexRepository.save(index);
        }
    }
}
