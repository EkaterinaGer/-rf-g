package searchengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CurrentRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleRuntime(CurrentRuntimeException ex) {
        return Map.of("result", false, "error", ex.getMessage());
    }

    @ExceptionHandler(CurrentIOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIO(CurrentIOException ex) {
        return Map.of("result", false, "error", ex.getMessage());
    }

    @ExceptionHandler(CurrentInterruptedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleInterrupted(CurrentInterruptedException ex) {
        return Map.of("result", false, "error", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("result", false, "error", ex.getMessage());
    }
    
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleIllegalState(IllegalStateException ex) {
        return Map.of("result", false, "error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleAll(Exception ex) {
        return Map.of("result", false, "error", "Unexpected error: " + ex.getMessage());
    }
}
