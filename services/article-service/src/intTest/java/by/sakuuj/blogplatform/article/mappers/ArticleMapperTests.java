package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.annotations.MapperTest;
import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.dtos.ArticleRequest;
import by.sakuuj.blogplatform.article.dtos.ArticleResponse;
import by.sakuuj.blogplatform.article.entities.jpa.ArticleEntity;
import by.sakuuj.blogplatform.article.entities.jpa.embeddable.ModificationAudit;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

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
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        UUID expectedAuthorId = UUID.fromString("7f978642-4c40-4898-82b1-3b4a66a21cbf");
        ArticleRequest requestDto = testDataBuilder.buildRequest();
        ArticleEntity expectedEntityButWithoutExpectedAuthor = testDataBuilder
                .withId(null)
                .withModificationAudit(new ModificationAudit())
                .withAuthor(null)
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
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        UUID authorId = UUID.fromString("7f978642-4c40-4898-82b1-3b4a66a21cbf");
        ArticleRequest requestDto = testDataBuilder.buildRequest();

        // when, then
       assertThatThrownBy(() -> articleMapper.toEntity(requestDto, authorId))
               .isInstanceOf(IllegalTransactionStateException.class);
    }



    @Test
    void shouldMapEntityToResponse_WithoutTransaction() {

        // given
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleEntity entity = testDataBuilder.build();
        ArticleResponse expectedResponse = testDataBuilder.buildResponse();

        // when
        ArticleResponse actualResponse = articleMapper.toResponse(entity, testDataBuilder.getTopics());

        // then
        assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
    }
}
