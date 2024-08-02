package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.dtos.ArticleRequest;
import by.sakuuj.blogplatform.article.dtos.ArticleResponse;
import by.sakuuj.blogplatform.article.entities.ArticleEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleMapperTests {

    private final ArticleMapper articleMapper = Mappers.getMapper(ArticleMapper.class);

    @Test
    void shouldMapRequestToEntity() {

        // given
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleRequest requestDto = testDataBuilder.buildRequest();
        ArticleEntity expectedEntity = testDataBuilder
                .withId(null)
                .withDatePublishedOn(null)
                .withDateUpdatedOn(null)
                .build();

        // when
        ArticleEntity actualEntity = articleMapper.toEntity(requestDto);

        // then
        assertThat(actualEntity).isEqualTo(expectedEntity);
    }

    @Test
    void shouldMapEntityToResponse() {

        // given
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleEntity entity = testDataBuilder.build();
        ArticleResponse expectedResponse = testDataBuilder.buildResponse();

        // when
        ArticleResponse actualResponse = articleMapper.toResponse(entity);

        // then
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }
}
