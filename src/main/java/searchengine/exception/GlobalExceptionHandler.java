package searchengine.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import searchengine.dto.responses.NotOkResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IndexingException.class)
    public NotOkResponse handleIndexingException(IndexingException ex) {
        log.error("Indexing error: {}", ex.getMessage());
        return new NotOkResponse(ex.getMessage());
    }

    @ExceptionHandler(SearchException.class)
    public NotOkResponse handleSearchException(SearchException ex) {
        log.error("Search error: {}", ex.getMessage());
        return new NotOkResponse(ex.getMessage());
    }

    @ExceptionHandler(StatisticsException.class)
    public NotOkResponse handleStatisticsException(StatisticsException ex) {
        log.error("Statistics error: {}", ex.getMessage());
        return new NotOkResponse(ex.getMessage());
    }

    @ExceptionHandler(LemmaException.class)
    public NotOkResponse handleLemmaException(LemmaException ex) {
        log.error("Lemma error: {}", ex.getMessage());
        return new NotOkResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public NotOkResponse handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return new NotOkResponse("Произошла непредвиденная ошибка");
    }
}
