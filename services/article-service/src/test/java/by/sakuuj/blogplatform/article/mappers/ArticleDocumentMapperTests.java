package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.dtos.ArticleSearchResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleDocumentMapperTests {

    private final ArticleDocumentMapper articleDocumentMapper = Mappers.getMapper(ArticleDocumentMapper.class);

    @Test
    void shouldMapDocumentToSearchResponse() {

        // given
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleDocument document = testDataBuilder.buildDocument();
        ArticleSearchResponse expectedSearchResponse = testDataBuilder.buildSearchResponse();

        // when
        ArticleSearchResponse actualSearchResponse = articleDocumentMapper.toSearchResponse(document);

        // then
        assertThat(actualSearchResponse)
                .isEqualTo(expectedSearchResponse);
    }
}
