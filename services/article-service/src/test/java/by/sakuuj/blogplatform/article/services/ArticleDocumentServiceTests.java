package by.sakuuj.blogplatform.article.services;

import by.sakuuj.blogplatform.article.ArticleServiceApplication;
import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.repository.PageView;
import by.sakuuj.blogplatform.article.repository.elasticsearch.ArticleDocumentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnableAutoConfiguration(exclude = {
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class
})
@SpringBootTest(classes = ArticleServiceApplication.class)
public class ArticleDocumentServiceTests {


    @MockBean
    private ArticleDocumentRepository articleDocumentRepository;

    @Autowired
    private ArticleDocumentService articleDocumentService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Test
    void shouldDeleteByIdUsingRepository() {
        // given
        UUID idToDeleteBy = UUID.fromString("f73be0a0-f00b-4fa5-8364-fc7e7e302663");
        doNothing().when(articleDocumentRepository).deleteById(idToDeleteBy);

        // when
        articleDocumentService.deleteById(idToDeleteBy);

        // then
        verify(articleDocumentRepository).deleteById(idToDeleteBy);
    }

    @Test
    void shouldThrowExceptionFromDeleteByIdWhenRepositoryThrowsException() {
        // given
        UUID idToDeleteBy = UUID.fromString("f73be0a0-f00b-4fa5-8364-fc7e7e302663");
        String errorMsg = "error when delete";
        doThrow(new RuntimeException(errorMsg)).when(articleDocumentRepository).deleteById(idToDeleteBy);

        // when, then
        assertThatThrownBy(() -> articleDocumentService.deleteById(idToDeleteBy))
                .hasMessage(errorMsg);

        verify(articleDocumentRepository).deleteById(idToDeleteBy);
    }

    @Test
    void shouldThrowExceptionWhenTryingToSaveWithNullId() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle()
                .withId(null);
        ArticleDocument articleDocumentToSave = testDataBuilder.buildDocument();

        // when, then
        assertThatThrownBy(() -> articleDocumentService.save(articleDocumentToSave));
    }

    @Test
    void shouldSaveUsingRepositoryAndThenConvertUsingMapper() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleDocument articleDocumentToSave = testDataBuilder.buildDocument();
        ArticleDocument savedArticleDocument = testDataBuilder.buildDocument();
        UUID expected = savedArticleDocument.getId();

        when(articleDocumentRepository.save(articleDocumentToSave)).thenReturn(savedArticleDocument);

        // when
        UUID actual = articleDocumentService.save(articleDocumentToSave);

        // then
        assertThat(actual).isEqualTo(expected);

        verify(articleDocumentRepository).save(articleDocumentToSave);
    }

    @Test
    void shouldThrowExceptionIfSaveThrowsException() {
        // given
        ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleDocument articleDocumentToSave = testDataBuilder.buildDocument();

        String errorMsg = "can not be saved msg";
        when(articleDocumentRepository.save(articleDocumentToSave)).thenThrow(new RuntimeException(errorMsg));

        // when, then
        assertThatThrownBy(() -> articleDocumentService.save(articleDocumentToSave))
                .hasMessage(errorMsg);

        verify(articleDocumentRepository).save(articleDocumentToSave);
    }

    @Test
    void shouldFindSortedByRelevanceUsingRepoAndThenConvertUsingMapper() {
        ArticleTestDataBuilder firstTestDataBuilder = ArticleTestDataBuilder.anArticle();
        ArticleTestDataBuilder secondTestDataBuilder = ArticleTestDataBuilder.anArticle()
                .withId(UUID.fromString("e1cfb957-f8a3-470e-b340-ca26563543c5"))
                .withTitle("SOME TITLE")
                .withContent("SOME CONTENT")
                .withTopics(List.of("TOPIC 1", "TOPIC 2", "etc."))
                .withDatePublishedOn(LocalDateTime.MIN)
                .withDateUpdatedOn(LocalDateTime.MIN.plusDays(1));

        UUID expectedFirstId = firstTestDataBuilder.getId();
        UUID expectedSecondId = secondTestDataBuilder.getId();

        String searchTerms = "some search terms";
        int pageNumber = 0;
        int pageSize = 10;

        when(articleDocumentRepository.findIdsOfDocsSortedByRelevance(eq(searchTerms), any(Pageable.class)))
                .thenReturn(PageView.<UUID>builder()
                        .content(List.of(expectedFirstId, expectedSecondId))
                        .size(pageSize)
                        .number(pageNumber)
                        .build()
                );

        // when
        PageView<UUID> actual = articleDocumentService.findSortedByRelevance(
                searchTerms, pageNumber, pageSize);

        // then
        assertThat(actual.number()).isEqualTo(pageNumber);
        assertThat(actual.size()).isEqualTo(pageSize);

        assertThat(actual.content()).containsExactly(expectedFirstId, expectedSecondId);

        verify(articleDocumentRepository).findIdsOfDocsSortedByRelevance(eq(searchTerms), pageableCaptor.capture());
        Pageable captured = pageableCaptor.getValue();

        assertThat(captured.getPageNumber()).isEqualTo(pageNumber);
        assertThat(captured.getPageSize()).isEqualTo(pageSize);
        assertThat(captured.getSort().isSorted()).isFalse();
    }

    @Test
    void shouldFindSortedByDatePublishedOnAndThenByRelevanceUsingRepoAndThenConvertUsingMapper() {
        ArticleTestDataBuilder firstTestDataBuilder = ArticleTestDataBuilder.anArticle();
        ArticleTestDataBuilder secondTestDataBuilder = ArticleTestDataBuilder.anArticle()
                .withId(UUID.fromString("e1cfb957-f8a3-470e-b340-ca26563543c5"))
                .withTitle("SOME TITLE")
                .withContent("SOME CONTENT")
                .withTopics(List.of("TOPIC 1", "TOPIC 2", "etc."))
                .withDatePublishedOn(LocalDateTime.MIN)
                .withDateUpdatedOn(LocalDateTime.MIN.plusDays(1));

        UUID expectedFirstId = firstTestDataBuilder.getId();
        UUID expectedSecondId = secondTestDataBuilder.getId();

        String searchTerms = "some search terms";
        int expectedPageNumber = 0;
        int expectedPageSize = 10;

        when(articleDocumentRepository.findIdsOfDocsSortedByRelevance(eq(searchTerms), any(Pageable.class)))
                .thenReturn(PageView.<UUID>builder()
                        .content(List.of(expectedFirstId, expectedSecondId))
                        .size(expectedPageSize)
                        .number(expectedPageNumber)
                        .build());

        // when
        PageView<UUID> actual = articleDocumentService.findSortedByDatePublishedOnAndThenByRelevance(
                searchTerms, expectedPageNumber, expectedPageSize);

        // then
        assertThat(actual.number()).isEqualTo(expectedPageNumber);
        assertThat(actual.size()).isEqualTo(expectedPageSize);

        assertThat(actual.content()).containsExactly(expectedFirstId, expectedSecondId);

        verify(articleDocumentRepository).findIdsOfDocsSortedByRelevance(eq(searchTerms), pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();

        assertThat(capturedPageable.getPageNumber()).isEqualTo(expectedPageNumber);
        assertThat(capturedPageable.getPageSize()).isEqualTo(expectedPageSize);
        assertThat(capturedPageable.getSort().isSorted()).isTrue();

        Sort.Order datePublishedOnOrder = capturedPageable.getSort()
                .getOrderFor(ArticleDocument.ElasticsearchFieldNames.DATE_PUBLISHED);
        assertThat(datePublishedOnOrder).isNotNull();

        boolean isSortedByDatePublishedOnDesc = datePublishedOnOrder.isDescending();
        assertThat(isSortedByDatePublishedOnDesc).isTrue();
    }
}
