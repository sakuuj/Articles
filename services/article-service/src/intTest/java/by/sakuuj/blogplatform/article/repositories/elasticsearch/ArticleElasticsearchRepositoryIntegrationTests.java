package by.sakuuj.blogplatform.article.repositories.elasticsearch;


import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.repositories.PageView;
import by.sakuuj.elasticsearch.IndexCreator;
import by.sakuuj.elasticsearch.IndexCreatorProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles
public class ArticleElasticsearchRepositoryIntegrationTests {

    private static final String ELASTICSEARCH_USERNAME = "elastic";
    private static final String ELASTICSEARCH_PASSWORD = "elastic1dfkdfjePASS";
    private static final String ELASTICSEARCH_INDEX_NAME = "articles";

    @Container
    private static final GenericContainer<?> ELASTICSEARCH_CONTAINER =
            new GenericContainer<>("elasticsearch:8.14.3")
                    .withEnv("ELASTIC_USER", ELASTICSEARCH_USERNAME)
                    .withEnv("ELASTIC_PASSWORD", ELASTICSEARCH_PASSWORD)
                    .withEnv("ES_SETTING_DISCOVERY_TYPE", "single-node")
                    .withEnv("ES_SETTING_XPACK_SECURITY_HTTP_SSL_ENABLED", "false")
                    .withExposedPorts(9200);

    @Autowired
    private IndexCreator indexCreator;
    @Autowired
    private IndexCreatorProperties indexCreatorProperties;

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

    private static String getFullContainerUri() {
        return ELASTICSEARCH_CONTAINER.getHost() + ":" + ELASTICSEARCH_CONTAINER.getFirstMappedPort();
    }

    @Autowired
    private ArticleElasticsearchRepository elasticsearchRepository;

    @BeforeEach
    void ss() {
        indexCreator.createIndexes(indexCreatorProperties.getIndexToJsonFilePairs());
    }

    @AfterEach
    void removeAllDocumentsFromIndex() {
        elasticsearchRepository.deleteAll(RefreshPolicy.IMMEDIATE);
        Iterable<ArticleDocument> allDocuments = elasticsearchRepository.findAll();
        assertThat(allDocuments.iterator().hasNext()).isFalse();
    }

    @Test
    void shouldFindDocumentWithRelevantSearchTerm() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();
        String searchTerm = "qwerty";

        int expectedPageNumber = 0;
        int expectedRequestedPageSize = 10;
        PageRequest pageable = PageRequest.of(expectedPageNumber, expectedRequestedPageSize);

        ArticleDocument expectedDocument = testDataBuilder
                .withTitle("TITLE_1 " + searchTerm)
                .withContent("CONTENT_1 " + searchTerm)
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        // when
        elasticsearchRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
        PageView<ArticleDocument> actualPage = elasticsearchRepository.findMostRelevantDocuments(searchTerm, pageable);


        // then
        assertThat(actualPage.content().size()).isEqualTo(1);
        assertThat(actualPage.content().getFirst()).isEqualTo(expectedDocument);

        assertThat(actualPage.pageNumber()).isEqualTo(expectedPageNumber);
        assertThat(actualPage.requestedSize()).isEqualTo(expectedRequestedPageSize);
    }

    @Test
    void shouldFindNoDocumentsWhenNoneArePresent() {
        // given
        String searchTerm = "qwerty";

        int expectedPageNumber = 0;
        int expectedRequestedPageSize = 10;
        PageRequest pageable = PageRequest.of(expectedPageNumber, expectedRequestedPageSize);

        // when
        PageView<ArticleDocument> actualPage = elasticsearchRepository.findMostRelevantDocuments(searchTerm, pageable);

        // then
        assertThat(actualPage.content()).isEmpty();
        assertThat(actualPage.pageNumber()).isEqualTo(expectedPageNumber);
        assertThat(actualPage.requestedSize()).isEqualTo(expectedRequestedPageSize);
    }

    @Test
    void shouldHaveFuzzinessPropertyOnQueryWithTitle() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        String searchTerm = "qwerty";
        String messedUpTerm = "qwerrr";

        int expectedPageNumber = 0;
        int expectedRequestedPageSize = 10;
        PageRequest pageable = PageRequest.of(expectedPageNumber, expectedRequestedPageSize);

        ArticleDocument expectedDocument = testDataBuilder
                .withTitle("TITLE_1 " + searchTerm)
                .withContent("CONTENT_1 ")
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        // when
        elasticsearchRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
        PageView<ArticleDocument> actualPage = elasticsearchRepository.findMostRelevantDocuments(messedUpTerm, pageable);

        // then
        assertThat(actualPage.content().size()).isEqualTo(1);
        assertThat(actualPage.content().getFirst()).isEqualTo(expectedDocument);

        assertThat(actualPage.pageNumber()).isEqualTo(expectedPageNumber);
        assertThat(actualPage.requestedSize()).isEqualTo(expectedRequestedPageSize);
    }

    @Test
    void shouldHaveFuzzinessPropertyOnQueryWithContent() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        String searchTerm = "qwerty";
        String messedUpTerm = "qwerrr";

        int expectedPageNumber = 0;
        int expectedRequestedPageSize = 10;
        PageRequest pageable = PageRequest.of(expectedPageNumber, expectedRequestedPageSize);

        ArticleDocument expectedDocument = testDataBuilder
                .withTitle("TITLE_1")
                .withContent("CONTENT_1 " + searchTerm)
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        // when
        elasticsearchRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
        PageView<ArticleDocument> actualPage = elasticsearchRepository.findMostRelevantDocuments(messedUpTerm, pageable);

        // then
        assertThat(actualPage.content().size()).isEqualTo(1);
        assertThat(actualPage.content().getFirst()).isEqualTo(expectedDocument);

        assertThat(actualPage.pageNumber()).isEqualTo(expectedPageNumber);
        assertThat(actualPage.requestedSize()).isEqualTo(expectedRequestedPageSize);
    }

    @Test
    void shouldReturnDocsSortedByScore() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        int expectedPageNumber = 0;
        int expectedRequestedPageSize = 10;
        PageRequest pageable = PageRequest.of(
                expectedPageNumber,
                expectedRequestedPageSize
        );

        String firstSearchTerm = "qwerty";
        ArticleDocument expectedDocumentWithLowerScore = testDataBuilder
                .withTitle("TITLE_1 " + firstSearchTerm)
                .withContent("CONTENT_1 " + firstSearchTerm)
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        String secondSearchTerm = "smth";
        ArticleDocument expectedDocumentWithHigherScore = testDataBuilder
                .withTitle("TITLE_2 " + firstSearchTerm + " " + secondSearchTerm)
                .withContent("CONTENT_2 " + firstSearchTerm + " " + secondSearchTerm)
                .withId(UUID.fromString("e1d3cf77-8d0d-4f26-bf4d-a11c5dfc3b06"))
                .buildDocument();

        // when
        elasticsearchRepository.saveAll(
                List.of(expectedDocumentWithHigherScore, expectedDocumentWithLowerScore),
                RefreshPolicy.IMMEDIATE
        );
        PageView<ArticleDocument> actualPage = elasticsearchRepository.findMostRelevantDocuments(firstSearchTerm, pageable);

        // then
        assertThat(actualPage.content().size()).isEqualTo(2);
        assertThat(actualPage.content().getFirst()).isEqualTo(expectedDocumentWithHigherScore);
        assertThat(actualPage.content().getLast()).isEqualTo(expectedDocumentWithLowerScore);

        assertThat(actualPage.pageNumber()).isEqualTo(expectedPageNumber);
        assertThat(actualPage.requestedSize()).isEqualTo(expectedRequestedPageSize);
    }

    @Test
    void shouldReturnCorrectPageNumberAndRequestedSize() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        int expectedPageNumber = 1;
        int expectedRequestedPageSize = 2;
        PageRequest pageable = PageRequest.of(expectedPageNumber, expectedRequestedPageSize);


        String searchTerm = "qwerty";

        ArticleDocument firstDocument = testDataBuilder
                .withTitle("TITLE_1 " + searchTerm)
                .withContent("CONTENT_1 " + searchTerm)
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        ArticleDocument secondDocument = testDataBuilder
                .withTitle("TITLE_2 " + searchTerm)
                .withContent("CONTENT_2 " + searchTerm)
                .withId(UUID.fromString("e1d3cf77-8d0d-4f26-bf4d-a11c5dfc3b06"))
                .buildDocument();

        ArticleDocument thirdDocument = testDataBuilder
                .withTitle("TITLE_3 " + searchTerm)
                .withContent("CONTENT_3 " + searchTerm)
                .withId(UUID.fromString("f893b137-0597-4391-a00e-a19452cc09da"))
                .buildDocument();

        // when
        elasticsearchRepository.saveAll(
                List.of(firstDocument, secondDocument, thirdDocument),
                RefreshPolicy.IMMEDIATE
        );
        PageView<ArticleDocument> actualPage = elasticsearchRepository.findMostRelevantDocuments(searchTerm, pageable);

        // then
        assertThat(actualPage.content().size()).isEqualTo(1);
        assertThat(actualPage.content().getFirst())
                .isIn(firstDocument, secondDocument, thirdDocument);

        assertThat(actualPage.pageNumber()).isEqualTo(expectedPageNumber);
        assertThat(actualPage.requestedSize()).isEqualTo(expectedRequestedPageSize);
    }
}