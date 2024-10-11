package by.sakuuj.elasticsearch.http.client;

import by.sakuuj.elasticsearch.IndexCreatorAutoConfiguration;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IndexCreatorAutoConfiguration.class})
class IndexCreatorElasticsearchClientImplIntegrationTests {

    private static final String ELASTICSEARCH_USERNAME = "elasticxxcz";
    private static final String ELASTICSEARCH_PASSWORD = "elastic1czz";
    private static final String ELASTICSEARCH_HOST = "localhost";

    @DynamicPropertySource
    static void setDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("by.sakuuj.elasticsearch.index-creator.password", () -> ELASTICSEARCH_PASSWORD);
        registry.add("by.sakuuj.elasticsearch.index-creator.username", () -> ELASTICSEARCH_USERNAME);
        registry.add("by.sakuuj.elasticsearch.index-creator.uri", () ->
                "http://" + ELASTICSEARCH_HOST + ":" + wireMock.getPort());
    }

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .failOnUnmatchedRequests(true)
            .build();

    @Autowired
    private IndexCreatorElasticsearchClientImpl elasticsearchClient;

    @BeforeAll
    static void configureWireMockClient() {
        WireMock.configureFor(ELASTICSEARCH_HOST, wireMock.getPort());
    }

    @Test
    void shouldRespondTrue_OnIndexExistsMethod_IfServerRespondsWith_200_status() {
        String indexName = "somename";

        String indexUrl = "/" + indexName;
        wireMock.stubFor(head(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMock.getPort())
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(200))
        );

        boolean indexExists = elasticsearchClient.indexExists(indexName);
        assertThat(indexExists).isTrue();

        wireMock.verify(exactly(1), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }

    @Test
    void shouldRespondFalse_OnIndexExistsMethod_IfServerRespondsWith_404_status() {
        String indexName = "somename";

        String indexUrl = "/" + indexName;
        wireMock.stubFor(head(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMock.getPort())
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(404))
        );

        boolean indexExists = elasticsearchClient.indexExists(indexName);
        assertThat(indexExists).isFalse();

        wireMock.verify(exactly(1), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 301, 401, 501})
    void shouldThrowException_OnIndexExistsMethod_IfServerRespondsWithStatusOtherThan_200_or_404(
            int incorrectStatus
    ) {
        String indexName = "somename";

        String indexUrl = "/" + indexName;
        wireMock.stubFor(head(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMock.getPort())
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(incorrectStatus))
        );

        assertThatThrownBy(() -> elasticsearchClient.indexExists(indexName));

        wireMock.verify(exactly(1), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201})
    void shouldNotThrow_OnCreateIndexMethod_IfServerRespondsWith_200_or_201_status(
            int successStatus
    ) {
        String indexName = "somename";
        String jsonQuery = "ignored";

        String indexUrl = "/" + indexName;
        wireMock.stubFor(put(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMock.getPort())
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse().withStatus(successStatus))
        );

        assertThatNoException().isThrownBy(() -> elasticsearchClient.createIndex(indexName, jsonQuery));

        wireMock.verify(exactly(1), WireMock.anyRequestedFor(WireMock.anyUrl()));

    }

    @Test
    void shouldThrow_OnCreateIndexMethod_IfServerRespondsWith_400_status() {
        String indexName = "somename";
        String jsonQuery = "";

        String indexUrl = "/" + indexName;
        wireMock.stubFor(put(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMock.getPort())
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse().withStatus(400))
        );

        assertThatThrownBy(() -> elasticsearchClient.createIndex(indexName, jsonQuery));

        wireMock.verify(exactly(1), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }
}
