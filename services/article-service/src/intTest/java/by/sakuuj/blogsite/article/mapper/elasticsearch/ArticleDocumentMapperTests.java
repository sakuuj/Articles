package by.sakuuj.blogsite.article.mapper.elasticsearch;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ArticleDocumentMapperTests {

    @TestConfiguration
    @ComponentScan(basePackages = "by.sakuuj.blogsite.article.mapper.elasticsearch")
    static class TestConfig {
    }

    @Autowired
    private ArticleDocumentMapper articleDocumentMapper;

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
}
