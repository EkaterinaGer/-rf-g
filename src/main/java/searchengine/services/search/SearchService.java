package searchengine.services.search;

import searchengine.dto.SearchDto;
import searchengine.dto.responses.ResultDto;
import java.util.List;

public interface SearchService {
    List<ResultDto> search(SearchDto searchDto);
}
