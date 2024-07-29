package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.dtos.ArticleRequest;
import by.sakuuj.blogplatform.article.dtos.ArticleResponse;
import by.sakuuj.blogplatform.article.dtos.ArticleSearchResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.entities.ArticleEntity;
import by.sakuuj.blogplatform.article.utils.TestUtils;
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
                .usingRecursiveComparison(TestUtils.COMPARISON_FOR_CHAR_ARRAY)
                .isEqualTo(expectedResponse);
    }

    @Test
    void shouldMapDocumentToSearchResponse() {

        // given
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleDocument document = testDataBuilder.buildDocument();
        ArticleSearchResponse expectedSearchResponse = testDataBuilder.buildSearchResponse();

        // when
        ArticleSearchResponse actualSearchResponse = articleMapper.toSearchResponse(document);

        // then
        assertThat(actualSearchResponse)
                .usingRecursiveComparison(TestUtils.COMPARISON_FOR_CHAR_ARRAY)
                .isEqualTo(expectedSearchResponse);
    }
}
