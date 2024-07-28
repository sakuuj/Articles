package by.sakuuj.blogplatform.article.database.schema;

import by.sakuuj.blogplatform.article.utils.FileUtils;
import by.sakuuj.blogplatform.article.utils.RestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"prod", "index-creator"})
public class ElasticsearchIndexCreator implements CommandLineRunner {

    private final RestClient restClient;
    private final ElasticsearchProperties esProperties;
    private final ElasticsearchIndexCreatorProperties indexCreatorProperties;

    @Override
    public void run(String... args) throws Exception {
        
        String schemelessEsUri = esProperties.getUris().getFirst();

        boolean sslEnabled = esProperties.getRestclient().getSsl().getBundle() != null;
        String scheme = sslEnabled ? "https://" : "http://";

        String uri = scheme + schemelessEsUri;

        Map<String, String> indexToJsonFileMappings = indexCreatorProperties.getIndexToJsonFileMappings();

        if (indexToJsonFileMappings == null) {
            log.info("No index-to-file mappings were found");
            return;
        }

        indexToJsonFileMappings.forEach(
                (index, filePath) -> {
                    if (indexExists(uri, index)) {
                        return;
                    }
                    createIndex(uri, index, filePath);
                }
        );
    }

    /**
     *
     * @param esUri uri of the elastic search host (scheme://host:port)
     * @param indexName name of the new index
     * @param pathToJson path to json file containing body of the create index request
     */
    private void createIndex(String esUri, String indexName, String pathToJson) {

        String destinationUri = esUri + "/" + indexName;

        RestClient.RequestBodySpec createRequest = restClient
                .put()
                .uri(destinationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(FileUtils.fileToString(pathToJson));

        ResponseEntity<String> createResponse = RestUtils.addBasicAuth(createRequest, esProperties.getUsername(), esProperties.getPassword())
                .retrieve()
                .onStatus(RestUtils::anyStatus, RestUtils.noOpHandler())
                .toEntity(String.class);

        if (createResponse.getStatusCode().is2xxSuccessful()) {
            log.info("'{}' index has been successfully created", indexName);
            return;
        }

        String errorMsg = String.format(
                "PUT %s ERROR: when creating elasticsearch index %s",
                destinationUri, indexName);

        log.error(errorMsg);
        throw new RuntimeException(errorMsg);
    }

    private boolean indexExists(String esUri, String indexName) {

        String destinationUri = esUri + "/" + indexName;

        RestClient.RequestHeadersSpec<?> existsRequest = restClient
                .head()
                .uri(destinationUri);

        ResponseEntity<Void> existsResponse = RestUtils.addBasicAuth(existsRequest, esProperties.getUsername(), esProperties.getPassword())
                .retrieve()
                .onStatus(RestUtils::anyStatus, RestUtils.noOpHandler())
                .toBodilessEntity();

        HttpStatusCode existsStatusCode = existsResponse.getStatusCode();

        if (existsStatusCode.isSameCodeAs(HttpStatus.OK)) {
            log.info("'{}' index has already been created", indexName);
            return true;
        }

        if (!existsStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND)) {

            String errorMsg = String.format("HEAD %s ERROR: received %s status code when checking existence " +
                            "of an index '%s' (allowed codes are: 200, 404)",
                    destinationUri, existsStatusCode, indexName);

            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        return false;
    }
}
