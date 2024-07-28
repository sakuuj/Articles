package by.sakuuj.blogplatform.article.database.schema;


import by.sakuuj.blogplatform.article.ArticleServiceApplication;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@ActiveProfiles("index-creator")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ArticleServiceApplication.class})
class ElasticsearchIndexCreatorIntegrationTests {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .failOnUnmatchedRequests(true)
            .build();

    private static final String ELASTICSEARCH_USERNAME = "elasticxxcz";
    private static final String ELASTICSEARCH_PASSWORD = "elastic1czz";
    private static final String ELASTICSEARCH_HOST = "localhost";

    private static final String INDEX_TO_CREATE = "itemsitems";
    private static final String CREATE_INDEX_QUERY_FILE = "database-schema/createIndex.json";

    @DynamicPropertySource
    static void setDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.password", () -> ELASTICSEARCH_PASSWORD);
        registry.add("spring.elasticsearch.username", () -> ELASTICSEARCH_USERNAME);
        registry.add("spring.elasticsearch.uris",
                () -> List.of(ELASTICSEARCH_HOST + ":" + wireMock.getPort()));
        registry.add("by.sakuuj.elasticsearch.index-to-json-file-mappings." + INDEX_TO_CREATE,
                () -> CREATE_INDEX_QUERY_FILE);
    }

    @Autowired
    private ElasticsearchIndexCreator indexCreator;

    @BeforeAll
    static void configureWireMockClient() {
        WireMock.configureFor(ELASTICSEARCH_HOST, wireMock.getPort());
    }

    @Test
    void shouldReturnWhenIndexAlreadyExists() {
        //given
        String indexUrl = "/" + INDEX_TO_CREATE;

        wireMock.stubFor(head(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMock.getPort())
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(200))
        );

        // when, then
        assertThatNoException().isThrownBy(() -> indexCreator.run());

        int headCount = 1;
        verify(exactly(headCount), headRequestedFor(urlEqualTo(indexUrl)));

        verify(exactly(headCount), anyRequestedFor(anyUrl()));
    }

    @Test
    void shouldSendCreateRequestIfIndexDoesNotExist() {
        //given
        int wireMockPort = wireMock.getPort();

        String indexUrl = "/" + INDEX_TO_CREATE;

        wireMock.stubFor(head(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMockPort)
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(404))
        );

        wireMock.stubFor(put(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMockPort)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(200))
        );

        // when, then
        assertThatNoException().isThrownBy(() -> indexCreator.run());

        int headCount = 1;
        int putCount = 1;
        wireMock.verify(exactly(headCount), WireMock.headRequestedFor(urlEqualTo(indexUrl)));
        wireMock.verify(exactly(putCount), WireMock.putRequestedFor(urlEqualTo(indexUrl)));

        int totalCount = headCount + putCount;
        wireMock.verify(exactly(totalCount), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }

    @Test
    void shouldThrowExceptionIfIndexDoesNotExistButCanNotCreate() {
        //given
        int wireMockPort = wireMock.getPort();

        String indexUrl = "/" + INDEX_TO_CREATE;

        wireMock.stubFor(head(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMockPort)
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(404))
        );

        wireMock.stubFor(put(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMockPort)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(400))
        );

        // when, then
        assertThatThrownBy(() -> indexCreator.run());

        int headCount = 1;
        int putCount = 1;
        wireMock.verify(exactly(headCount), WireMock.headRequestedFor(urlEqualTo(indexUrl)));
        wireMock.verify(exactly(putCount), WireMock.putRequestedFor(urlEqualTo(indexUrl)));

        int totalCount = headCount + putCount;
        wireMock.verify(exactly(totalCount), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }

    @Test
    void shouldThrowExceptionIfExistsRequestReturnStatusCodeOtherThan_200_or_404() {
        //given
        int wireMockPort = wireMock.getPort();

        String indexUrl = "/" + INDEX_TO_CREATE;

        int statusOtherThan_200_or_404 = 403;

        wireMock.stubFor(head(urlEqualTo(indexUrl))
                .withHost(equalTo(ELASTICSEARCH_HOST))
                .withPort(wireMockPort)
                .withBasicAuth(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)
                .willReturn(aResponse().withStatus(statusOtherThan_200_or_404))
        );


        // when, then
        assertThatThrownBy(() -> indexCreator.run());

        int headCount = 1;
        wireMock.verify(exactly(headCount), WireMock.headRequestedFor(urlEqualTo(indexUrl)));

        wireMock.verify(exactly(headCount), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }
}
