package by.sakuuj.blogsite.article.mapper.elasticsearch;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleDocumentMapperTests {

    private final ArticleDocumentMapperImpl articleDocumentMapper = new ArticleDocumentMapperImpl();

    @Test
    void shouldMapToDocument() {

        // given
        var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
        ArticleEntity entityToMap = articleTestDataBuilder.build();
        ArticleDocument expected = articleTestDataBuilder.buildDocument();

        // when
        ArticleDocument actual = articleDocumentMapper.toDocument(entityToMap);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldMapResponseToDocument() {

        // given
        var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
        ArticleResponse response = articleTestDataBuilder.buildResponse();
        ArticleDocument expected = articleTestDataBuilder.buildDocument();

        // when
        ArticleDocument actual = articleDocumentMapper.toDocument(response);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
