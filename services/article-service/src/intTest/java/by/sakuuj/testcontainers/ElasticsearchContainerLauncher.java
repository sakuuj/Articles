package by.sakuuj.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
public abstract class ElasticsearchContainerLauncher {

    static final String ELASTICSEARCH_USERNAME = "elastic";
    static final String ELASTICSEARCH_PASSWORD = "elastic1dfkdfjePASS";
    static final String ELASTICSEARCH_INDEX_NAME = "articles";

    @Container
    static final GenericContainer<?> ELASTICSEARCH_CONTAINER =
            new GenericContainer<>("elasticsearch:8.14.3")
                    .withEnv("ELASTIC_PASSWORD", ELASTICSEARCH_PASSWORD)
                    .withEnv("ES_SETTING_DISCOVERY_TYPE", "single-node")
                    .withEnv("ES_SETTING_XPACK_SECURITY_HTTP_SSL_ENABLED", "false")
                    .withExposedPorts(9200);

    @DynamicPropertySource
    static void setDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", () -> List.of(getFullContainerUri()));
        registry.add("by.sakuuj.elasticsearch.index-creator.uri", () -> "http://" + getFullContainerUri());

        registry.add("by.sakuuj.elasticsearch.index-creator.username", () -> ELASTICSEARCH_USERNAME);
        registry.add("spring.elasticsearch.username", () -> ELASTICSEARCH_USERNAME);

        registry.add("by.sakuuj.elasticsearch.index-creator.password", () -> ELASTICSEARCH_PASSWORD);
        registry.add("spring.elasticsearch.password", () -> ELASTICSEARCH_PASSWORD);

        registry.add("by.sakuuj.elasticsearch.index-creator.index-to-json-file-pairs", () -> List.of(
                ELASTICSEARCH_INDEX_NAME + "<->repositories/elasticsearch/createArticlesIndex.json"
        ));
    }

    static String getFullContainerUri() {
        return ELASTICSEARCH_CONTAINER.getHost() + ":" + ELASTICSEARCH_CONTAINER.getFirstMappedPort();
    }

}
