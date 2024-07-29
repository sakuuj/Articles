package by.sakuuj.blogplatform.article.repositories.elasticsearch;


import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@ActiveProfiles("index-creator")
public class ArticleElasticsearchRepositoryIntegrationTests {

    private static final String ELASTICSEARCH_USERNAME = "elastic";
    private static final String ELASTICSEARCH_PASSWORD = "elastic1dfkdfjePASS";

    @Container
    private static final GenericContainer<?> ELASTICSEARCH_CONTAINER =
            new GenericContainer<>("elasticsearch:8.14.3")
                    .withEnv("ELASTIC_USER", ELASTICSEARCH_USERNAME)
                    .withEnv("ELASTIC_PASSWORD", ELASTICSEARCH_PASSWORD)
                    .withEnv("ES_SETTING_DISCOVERY_TYPE", "single-node")
                    .withEnv("ES_SETTING_XPACK_SECURITY_HTTP_SSL_ENABLED", "false")
                    .withExposedPorts(9200);

    @DynamicPropertySource
    static void setDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.password", () -> ELASTICSEARCH_PASSWORD);
        registry.add("spring.elasticsearch.username", () -> ELASTICSEARCH_USERNAME);
        registry.add("spring.elasticsearch.uris", () -> List.of(getFullContainerUri()));
    }

    private static String getFullContainerUri() {
        return ELASTICSEARCH_CONTAINER.getHost() + ":" + ELASTICSEARCH_CONTAINER.getFirstMappedPort();
    }

    @Autowired
    private ArticleElasticsearchRepository elasticsearchRepository;

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
        PageRequest pageable = PageRequest.of(0, 10);

        ArticleDocument expectedDocument = testDataBuilder
                .withTitle("TITLE_1 " + searchTerm)
                .withContent(("CONTENT_1 " + searchTerm).toCharArray())
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        // when
        elasticsearchRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
        List<ArticleDocument> actualContent = elasticsearchRepository.findMostRelevantDocuments(searchTerm, pageable)
                .content();


        // then
        assertThat(actualContent.size()).isEqualTo(1);

        assertThat(actualContent.getFirst())
                .usingRecursiveComparison(TestUtils.COMPARISON_FOR_CHAR_ARRAY)
                .isEqualTo(expectedDocument);
    }

    @Test
    void shouldFindNoDocumentsWhenNoneArePresent() {
        // given
        String searchTerm = "qwerty";
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        List<ArticleDocument> actualContent = elasticsearchRepository.findMostRelevantDocuments(searchTerm, pageable)
                .content();

        // then
        assertThat(actualContent).isEmpty();
    }

    @Test
    void shouldHaveFuzzinessPropertyOnQueryWithTitle() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        String searchTerm = "qwerty";
        String messedUpTerm = "qwerrr";

        PageRequest pageable = PageRequest.of(0, 10);

        ArticleDocument expectedDocument = testDataBuilder
                .withTitle("TITLE_1 " + searchTerm)
                .withContent("CONTENT_1 ".toCharArray())
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        // when
        elasticsearchRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
        List<ArticleDocument> actualContent = elasticsearchRepository.findMostRelevantDocuments(messedUpTerm, pageable)
                .content();

        // then
        assertThat(actualContent.size()).isEqualTo(1);

        assertThat(actualContent.getFirst())
                .usingRecursiveComparison(TestUtils.COMPARISON_FOR_CHAR_ARRAY)
                .isEqualTo(expectedDocument);
    }

    @Test
    void shouldHaveFuzzinessPropertyOnQueryWithContent() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        String searchTerm = "qwerty";
        String messedUpTerm = "qwerrr";

        PageRequest pageable = PageRequest.of(0, 10);

        ArticleDocument expectedDocument = testDataBuilder
                .withTitle("TITLE_1")
                .withContent(("CONTENT_1 " + searchTerm).toCharArray())
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        // when
        elasticsearchRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
        List<ArticleDocument> actualContent = elasticsearchRepository.findMostRelevantDocuments(messedUpTerm, pageable)
                .content();

        // then
        assertThat(actualContent.size()).isEqualTo(1);

        assertThat(actualContent.getFirst())
                .usingRecursiveComparison(TestUtils.COMPARISON_FOR_CHAR_ARRAY)
                .isEqualTo(expectedDocument);
    }

    @Test
    void shouldReturnDocsSortedIfSortProvided() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        PageRequest pageable = PageRequest.of(0, 10, Sort.by("_score"));

        String firstSearchTerm = "qwerty";
        ArticleDocument expectedDocumentWithLowerScore = testDataBuilder
                .withTitle("TITLE_1 " + firstSearchTerm)
                .withContent(("CONTENT_1 " + firstSearchTerm).toCharArray())
                .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                .buildDocument();

        String secondSearchTerm = "smth";
        ArticleDocument expectedDocumentWithHigherScore = testDataBuilder
                .withTitle("TITLE_2 " + firstSearchTerm + " " + secondSearchTerm)
                .withContent(("CONTENT_2 " + firstSearchTerm + " " + secondSearchTerm).toCharArray())
                .withId(UUID.fromString("e1d3cf77-8d0d-4f26-bf4d-a11c5dfc3b06"))
                .buildDocument();

        // when
        elasticsearchRepository.saveAll(
                List.of(expectedDocumentWithHigherScore, expectedDocumentWithLowerScore),
                RefreshPolicy.IMMEDIATE
        );
        List<ArticleDocument> actualContent = elasticsearchRepository.findMostRelevantDocuments(firstSearchTerm, pageable)
                .content();

        // then
        assertThat(actualContent.size()).isEqualTo(2);

        assertThat(actualContent.getFirst())
                .usingRecursiveComparison(TestUtils.COMPARISON_FOR_CHAR_ARRAY)
                .isEqualTo(expectedDocumentWithHigherScore);

        assertThat(actualContent.getLast())
                .usingRecursiveComparison(TestUtils.COMPARISON_FOR_CHAR_ARRAY)
                .isEqualTo(expectedDocumentWithLowerScore);
    }
}


