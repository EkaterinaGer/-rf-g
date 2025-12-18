## Current Findings
- Controllers return plain bodies; no ResponseEntity used in controllers (`src/main/java/searchengine/controllers/ApiController.java`).
- Global exception handling uses `ResponseEntity` and sets status codes (`src/main/java/searchengine/exception/GlobalExceptionHandler.java:13–41`).
- Constructor injection is applied; `@Autowired` is absent in edited services. Some classes still have manual constructors instead of `@RequiredArgsConstructor`.
- API verbs mismatch the spec: `startIndexing` and `stopIndexing` are `GET`, while the spec requires `POST`.
- `indexPage` controller contains minor logic (deriving `siteUrl`), which should live in the service.

## Changes To Implement
### Controllers
- Change `@GetMapping("/startIndexing")` → `@PostMapping("/startIndexing")` and support optional `url` param; if provided, run single-site indexing, else run all-from-config.
- Change `@GetMapping("/stopIndexing")` → `@PostMapping("/stopIndexing")`.
- Remove site URL extraction from controller `indexPage`; pass only `url` to service.

### Services
- Update `IndexingService#indexPage` to accept only `url`; compute `siteUrl` inside the service.
- Ensure `CrawlingService` supports both full indexing and single-site indexing based on param.

### Exception Handling
- Replace `ResponseEntity` returns in `GlobalExceptionHandler` with DTO/Map + `@ResponseStatus` to meet the “no ResponseEntity” requirement globally.
- Keep error bodies as `{"result": false, "error": "..."}`.

### Dependency Injection
- Convert eligible components to `@RequiredArgsConstructor` with `private final` fields (controllers, services), removing manual constructors where safe.
- If Lombok annotation processing causes build issues, fallback to manual constructors (still compliant with the spec: constructor injection, `private final`).

### API Contract Consistency
- Ensure all endpoints return `{"result": true}` or the documented structures for `/api/statistics` and `/api/search`.
- Verify `StatisticsResponse` structure matches the UI expectations and the spec.

### Verification
- Rebuild the project.
- Run locally and validate endpoints:
  - `POST /api/startIndexing?url=https://www.playback.ru`
  - `POST /api/stopIndexing`
  - `POST /api/indexPage?url=...`
  - `GET /api/search?query=...&site=...`
  - `GET /api/statistics`
- Confirm dashboard no longer shows undefined and calls succeed with expected codes.

## Notes
- Using `@RequiredArgsConstructor` is preferred; if your toolchain conflicts with Lombok, constructor injection without Lombok remains fully compliant.
- I will perform the changes and re-verify the app end-to-end once approved.