package by.sakuuj.articles.article.mapper.elasticsearch;

import by.sakuuj.articles.article.ArticleTestDataBuilder;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
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
