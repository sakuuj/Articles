package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.ArticleTestDataBuilder;
import by.sakuuj.blogplatform.article.dtos.ArticleDocumentResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleDocumentMapperTests {

    private final ArticleDocumentMapper articleDocumentMapper = Mappers.getMapper(ArticleDocumentMapper.class);

    @Test
    void shouldMapDocumentToResponse() {

        // given
        var testDataBuilder = ArticleTestDataBuilder.anArticle();

        ArticleDocument document = testDataBuilder.buildDocument();
        ArticleDocumentResponse expectedSearchResponse = testDataBuilder.buildDocumentResponse();

        // when
        ArticleDocumentResponse actualSearchResponse = articleDocumentMapper.toResponse(document);

        // then
        assertThat(actualSearchResponse)
                .isEqualTo(expectedSearchResponse);
    }
}
