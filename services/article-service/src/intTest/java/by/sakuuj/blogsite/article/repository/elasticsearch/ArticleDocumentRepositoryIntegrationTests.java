package by.sakuuj.blogsite.article.repository.elasticsearch;


import by.sakuuj.annotations.ElasticsearchTest;
import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.paging.PageView;
import by.sakuuj.testcontainers.ElasticsearchSingletonContainerLauncher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@ElasticsearchTest
class ArticleDocumentRepositoryIntegrationTests extends ElasticsearchSingletonContainerLauncher {

    @SpyBean
    private SearchHitsToPageViewMapper searchHitsToPageViewMapper;

    @Captor
    private ArgumentCaptor<SearchHits<ArticleDocument>> hitsArgumentCaptor;

    @Autowired
    private ArticleDocumentRepository articleDocumentRepository;

    @DynamicPropertySource
    static void setDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("by.sakuuj.elasticsearch.index-creator.enable", () -> "true");
        registry.add("by.sakuuj.elasticsearch.index-creator.uri", () -> "http://" + getFullContainerUri());
        registry.add("by.sakuuj.elasticsearch.index-creator.username", () -> ELASTICSEARCH_USERNAME);
        registry.add("by.sakuuj.elasticsearch.index-creator.password", () -> ELASTICSEARCH_PASSWORD);
        registry.add("by.sakuuj.elasticsearch.index-creator.index-to-json-file-pairs", () -> List.of(
                ELASTICSEARCH_INDEX_NAME + "<->repositories/elasticsearch/createArticlesIndex.json"
        ));
    }

    @AfterEach
    void removeAllDocumentsFromIndex() {
        articleDocumentRepository.deleteAll(RefreshPolicy.IMMEDIATE);
        Iterable<ArticleDocument> allDocuments = articleDocumentRepository.findAll();
        assertThat(allDocuments.iterator().hasNext()).isFalse();
    }

    @Nested
    class findIdsOfDocsSortedByRelevance_String_Pageable {

        @Test
        void shouldReturnOnlyIdField() {
            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();
            String searchTerm = "qwerty";

            int expectedPageNumber = 0;
            int expectedRequestedPageSize = 10;
            PageRequest pageable = PageRequest.of(expectedPageNumber, expectedRequestedPageSize);

            UUID expectedId = UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4");
            ArticleDocument documentToSave = testDataBuilder
                    .withTitle("TITLE_1 " + searchTerm)
                    .withContent("CONTENT_1 " + searchTerm)
                    .withId(expectedId)
                    .buildDocument();

            ArticleDocument expected = testDataBuilder
                    .withId(expectedId)
                    .withTitle(null)
                    .withContent(null)
                    .withDatePublishedOn(null)
                    .buildDocument();

            // when
            articleDocumentRepository.save(documentToSave, RefreshPolicy.IMMEDIATE);
            articleDocumentRepository.findIdsOfDocsSortedByRelevance(searchTerm, pageable);

            // then
            verify(searchHitsToPageViewMapper).map(hitsArgumentCaptor.capture(), any(Pageable.class), any());

            ArticleDocument actual = hitsArgumentCaptor.getValue().getSearchHits().getFirst().getContent();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldReturnCorrectPageView() {
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
            articleDocumentRepository.saveAll(
                    List.of(firstDocument, secondDocument, thirdDocument),
                    RefreshPolicy.IMMEDIATE
            );
            PageView<UUID> actualPage = articleDocumentRepository.findIdsOfDocsSortedByRelevance(searchTerm, pageable);

            // then
            assertThat(actualPage.content().size()).isEqualTo(1);
            assertThat(actualPage.content().getFirst())
                    .isIn(firstDocument.getId(), secondDocument.getId(), thirdDocument.getId());

            assertThat(actualPage.number()).isEqualTo(expectedPageNumber);
            assertThat(actualPage.size()).isEqualTo(expectedRequestedPageSize);
        }

        @Test
        void shouldFindIdOfDocumentWithRelevantSearchTerm() {
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
            articleDocumentRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
            PageView<UUID> actualPage = articleDocumentRepository.findIdsOfDocsSortedByRelevance(searchTerm, pageable);

            // then
            assertThat(actualPage.content().size()).isEqualTo(1);
            assertThat(actualPage.content().getFirst()).isEqualTo(expectedDocument.getId());
        }

        @Test
        void shouldFindNoIdsWhenNoDocsArePresent() {
            // given
            String searchTerm = "qwerty";

            int expectedPageNumber = 0;
            int expectedRequestedPageSize = 10;
            PageRequest pageable = PageRequest.of(expectedPageNumber, expectedRequestedPageSize);

            // when
            PageView<UUID> actualPage = articleDocumentRepository.findIdsOfDocsSortedByRelevance(searchTerm, pageable);

            // then
            assertThat(actualPage.content()).isEmpty();
        }

        @Test
        void shouldHaveFuzzinessPropertyOnQuery_OnFieldNamedTitle() {
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
            articleDocumentRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
            PageView<UUID> actualPage = articleDocumentRepository.findIdsOfDocsSortedByRelevance(messedUpTerm, pageable);

            // then
            assertThat(actualPage.content().size()).isEqualTo(1);
            assertThat(actualPage.content().getFirst()).isEqualTo(expectedDocument.getId());
        }

        @Test
        void shouldHaveFuzzinessPropertyOnQuery_OnFieldNamedContent() {
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
            articleDocumentRepository.save(expectedDocument, RefreshPolicy.IMMEDIATE);
            PageView<UUID> actualPage = articleDocumentRepository.findIdsOfDocsSortedByRelevance(messedUpTerm, pageable);

            // then
            assertThat(actualPage.content().size()).isEqualTo(1);
            assertThat(actualPage.content().getFirst()).isEqualTo(expectedDocument.getId());
        }

        @ParameterizedTest
        @ValueSource(booleans = {false, true})
        void shouldReturnIdsSortedByScoreOfDocs(boolean lowerScoreDocComesFirst) {
            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            int expectedPageNumber = 0;
            int expectedRequestedPageSize = 10;
            PageRequest pageable = PageRequest.of(
                    expectedPageNumber,
                    expectedRequestedPageSize
            );

            String firstSearchTerm = "qwerty";
            ArticleDocument lowerScoreDoc = testDataBuilder
                    .withTitle("TITLE_1 " + firstSearchTerm)
                    .withContent("CONTENT_1 " + firstSearchTerm)
                    .withId(UUID.fromString("920e3446-ce92-4216-85d5-c7b9e76423d4"))
                    .buildDocument();

            String secondSearchTerm = "smth";
            ArticleDocument higherScoreDoc = testDataBuilder
                    .withTitle("TITLE_2 " + firstSearchTerm + " " + secondSearchTerm)
                    .withContent("CONTENT_2 " + firstSearchTerm + " " + secondSearchTerm)
                    .withId(UUID.fromString("e1d3cf77-8d0d-4f26-bf4d-a11c5dfc3b06"))
                    .buildDocument();

            List<ArticleDocument> docsToSave = new ArrayList<>(List.of(
                    higherScoreDoc, lowerScoreDoc
            ));
            if (lowerScoreDocComesFirst) {
                Collections.reverse(docsToSave);
            }

            // when
            articleDocumentRepository.saveAll(
                    docsToSave,
                    RefreshPolicy.IMMEDIATE
            );

            String searchTerms = firstSearchTerm + " " + secondSearchTerm;
            PageView<UUID> actualPage = articleDocumentRepository.findIdsOfDocsSortedByRelevance(searchTerms, pageable);

            // then
            assertThat(actualPage.content().size()).isEqualTo(2);
            assertThat(actualPage.content().getFirst()).isEqualTo(higherScoreDoc.getId());
            assertThat(actualPage.content().getLast()).isEqualTo(lowerScoreDoc.getId());
        }
    }
}