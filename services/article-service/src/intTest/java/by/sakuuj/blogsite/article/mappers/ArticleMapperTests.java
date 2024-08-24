package by.sakuuj.blogsite.article.mappers;

import by.sakuuj.annotations.MapperTest;
import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.ModificationAudit;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MapperTest
class ArticleMapperTests {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TransactionTemplate txTemplate;

    @Test
    void shouldMapRequestToEntity_WhenInTransaction() {

        // given
        var testBuilder = ArticleTestDataBuilder.anArticle();

        UUID expectedAuthorId = UUID.fromString("7f978642-4c40-4898-82b1-3b4a66a21cbf");
        ArticleRequest requestDto = testBuilder.buildRequest();
        ArticleEntity expectedEntityButWithoutExpectedAuthor = testBuilder
                .withId(null)
                .withAuthor(null)
                .withVersion((short) 0)
                .withArticleTopics(List.of())
                .withModificationAudit(new ModificationAudit())
                .build();

        // when
        ArticleEntity actualEntity = txTemplate.execute((tx) ->
                articleMapper.toEntity(requestDto, expectedAuthorId)
        );

        // then
        assertThat(actualEntity).usingRecursiveComparison()
                .ignoringFields("author")
                .isEqualTo(expectedEntityButWithoutExpectedAuthor);

        assertThat(actualEntity.getAuthor()).isNotNull();
        assertThat(actualEntity.getAuthor().getId()).isEqualTo(expectedAuthorId);
        assertThat(Hibernate.isInitialized(actualEntity.getAuthor())).isFalse();
    }

    @Test
    void shouldThrow_WhenMapRequestToEntity_AndOutOfTransaction() {

        // given
        var testBuilder = ArticleTestDataBuilder.anArticle();

        UUID authorId = UUID.fromString("7f978642-4c40-4898-82b1-3b4a66a21cbf");
        ArticleRequest requestDto = testBuilder.buildRequest();

        // when, then
       assertThatThrownBy(() -> articleMapper.toEntity(requestDto, authorId))
               .isInstanceOf(IllegalTransactionStateException.class);
    }



    @Test
    void shouldMapEntityToResponse_WithoutTransaction() {

        // given
        var testBuilder = ArticleTestDataBuilder.anArticle();

        ArticleEntity entity = testBuilder.build();
        ArticleResponse expectedResponse = testBuilder.buildResponse();

        // when
        ArticleResponse actualResponse = articleMapper.toResponse(entity);

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void shouldUpdateEntity() {

        // given
        var testBuilder = ArticleTestDataBuilder.anArticle();

        ArticleEntity articleToUpdate = testBuilder
                .withTitle("old title")
                .withContent("old content")
                .build();

        ArticleTestDataBuilder testBuilderUpdated = testBuilder
                .withTitle("new title")
                .withContent("new content");

        ArticleRequest updateRequest = testBuilderUpdated.buildRequest();
        ArticleEntity expected = testBuilderUpdated.build();

        // when
        articleMapper.updateEntity(articleToUpdate, updateRequest);

        // then
        assertThat(articleToUpdate).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
