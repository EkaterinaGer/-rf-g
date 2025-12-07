package searchengine.search;

import org.springframework.stereotype.Service;
import searchengine.dto.SearchDto;
import searchengine.dto.responses.ResultDto;
import searchengine.services.search.SearchService;

import java.util.List;

@Service
public record SearchStarter(SearchService searchService) {

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
}
