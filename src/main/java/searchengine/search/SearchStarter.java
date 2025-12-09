package searchengine.search;

import org.springframework.stereotype.Service;
import searchengine.dto.SearchDto;
import searchengine.dto.responses.ResultDto;
import searchengine.services.search.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SearchStarter {

    private final SearchService searchService;
    private final AtomicBoolean indexing = new AtomicBoolean(false);
    private final List<String> sites = new ArrayList<>();

    public SearchStarter(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<ResultDto> getSearchFromOneSite(String query, String siteUrl, int page, int size) {
        SearchDto searchDto = new SearchDto();
        searchDto.setQuery(query);
        searchDto.setSite(siteUrl);
        searchDto.setPage(page);
        searchDto.setSize(size);
        return searchService.search(searchDto);
    }

    public List<ResultDto> getFullSearch(String query, int page, int size) {
        SearchDto searchDto = new SearchDto();
        searchDto.setQuery(query);
        searchDto.setPage(page);
        searchDto.setSize(size);
        return searchService.search(searchDto);
    }

    public boolean startIndexing(String url) {
        if (indexing.compareAndSet(false, true)) {
            if (!sites.contains(url)) sites.add(url);

            new Thread(() -> {
                searchService.indexSite(url);
                indexing.set(false);
            }).start();

            return true;
        }
        return false;
    }

    public boolean stopIndexing() {
        if (indexing.get()) {
            indexing.set(false);
            return true;
        }
        return false;
    }

    public String getStatus() {
        return indexing.get() ? "Индексация в процессе" : "Индексация не запущена";
    }

    public List<String> getSites() {
        return new ArrayList<>(sites);
    }
}
