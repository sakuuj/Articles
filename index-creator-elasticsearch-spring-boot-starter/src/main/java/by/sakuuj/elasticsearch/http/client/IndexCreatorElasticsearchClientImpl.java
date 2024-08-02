package by.sakuuj.elasticsearch.http.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class IndexCreatorElasticsearchClientImpl implements IndexCreatorElasticsearchClient {

    private final RestClient restClient;
    private final HttpRequestAuthenticationHandler authenticationHandler;
    private final String uri;

    @Override
    public boolean indexExists(String indexName) {
        ResponseEntity<Void> existsResponse = performIndexExistsRequest(indexName);
        return processIndexExistsResponse(indexName, existsResponse);
    }

    private ResponseEntity<Void> performIndexExistsRequest(String indexName) {
        RestClient.RequestHeadersSpec<?> indexExistsRequest = restClient
                .head()
                .uri(uri + "/" + indexName);

        authenticationHandler.authenticate(indexExistsRequest);

        return indexExistsRequest
                .retrieve()
                .onStatus(s -> true, (request, response) -> {
                })
                .toBodilessEntity();
    }

    private boolean processIndexExistsResponse(String indexName, ResponseEntity<Void> existsResponse) {
        HttpStatusCode existsStatusCode = existsResponse.getStatusCode();

        if (existsStatusCode.isSameCodeAs(HttpStatus.OK)) {
            log.info("'{}' index has already been created", indexName);
            return true;
        }

        if (!existsStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND)) {

            String errorMsg = String.format("HEAD %s ERROR received %s status code when checking existence " +
                            "of an index '%s' (allowed codes are: 200, 404)",
                    uri, existsStatusCode, indexName);

            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        return false;
    }

    @Override
    public void createIndex(String indexName, String jsonQuery) {
        String destinationUri = uri + "/" + indexName;
        ResponseEntity<String> createIndexResponse = performCreateIndexRequest(destinationUri, jsonQuery);
        processCreateIndexResponse(indexName, createIndexResponse, destinationUri);
    }

    private ResponseEntity<String> performCreateIndexRequest(String destinationUri, String jsonQuery) {

        RestClient.RequestBodySpec createIndexRequest = restClient
                .put()
                .uri(destinationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonQuery);

        authenticationHandler.authenticate(createIndexRequest);

        return createIndexRequest
                .retrieve()
                .onStatus(s -> true, (request, response) -> {
                })
                .toEntity(String.class);
    }

    private static void processCreateIndexResponse(String indexName, ResponseEntity<String> createIndexResponse, String destinationUri) {
        if (createIndexResponse.getStatusCode().is2xxSuccessful()) {
            log.info("'{}' index has been successfully created", indexName);
            return;
        }

        String errorMsg = String.format(
                "PUT %s ERROR when creating elasticsearch index %s : %s",
                destinationUri, indexName, createIndexResponse.getBody());

        log.error(errorMsg);
        throw new RuntimeException(errorMsg);
    }
}
