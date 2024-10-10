package by.sakuuj.blogsite.dto.validator;

import by.sakuuj.blogsite.article.ArticleDocumentTestBuilder;
import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.testconfigs.EmptyConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(EmptyConfig.class)
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
class ValidArticleDocumentRequestValidatorTests {

    @Autowired
    private Validator validator;

    @Nested
    class delete_request {

        @Test
        void shouldReject_OnRequestTypeDelete_AndNotNullPayload() {

            // given
            ArticleDocument notNullDocument = ArticleDocumentTestBuilder.anArticleDocument().buildDocument();

            ArticleDocumentRequest invalidReq = ArticleDocumentRequest.builder()
                    .type(ArticleDocumentRequest.RequestType.DELETE)
                    .articleDocument(notNullDocument)
                    .build();

            // when
            Set<ConstraintViolation<ArticleDocumentRequest>> violations = validator.validate(invalidReq);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        void shouldAccept_OnRequestTypeDelete_AndNullPayload() {

            // given
            ArticleDocumentRequest invalidReq = ArticleDocumentRequest.builder()
                    .type(ArticleDocumentRequest.RequestType.DELETE)
                    .articleDocument(null)
                    .build();

            // when
            Set<ConstraintViolation<ArticleDocumentRequest>> violations = validator.validate(invalidReq);

            // then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    class upsert_request {

        @Test
        void shouldReject_OnRequestTypeUpsert_AndNullPayload() {

            // given
            ArticleDocumentRequest invalidReq = ArticleDocumentRequest.builder()
                    .type(ArticleDocumentRequest.RequestType.UPSERT)
                    .articleDocument(null)
                    .build();

            // when
            Set<ConstraintViolation<ArticleDocumentRequest>> violations = validator.validate(invalidReq);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        void shouldAccept_OnRequestTypeUpsert_AndNotNullPayload() {

            // given
            ArticleDocument notNullDocument = ArticleDocumentTestBuilder.anArticleDocument().buildDocument();

            ArticleDocumentRequest invalidReq = ArticleDocumentRequest.builder()
                    .type(ArticleDocumentRequest.RequestType.UPSERT)
                    .articleDocument(notNullDocument)
                    .build();

            // when
            Set<ConstraintViolation<ArticleDocumentRequest>> violations = validator.validate(invalidReq);

            // then
            assertThat(violations).isEmpty();
        }
    }
}
