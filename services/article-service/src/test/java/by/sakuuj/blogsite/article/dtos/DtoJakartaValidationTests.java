package by.sakuuj.blogsite.article.dtos;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.CommentTestDataBuilder;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoJakartaValidationTests {

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static Validator validator;

    @Nested
    class ArticleRequestDTO {

        private static final int MAX_TITLE_LENGTH = 100;
        private static final int MIN_TITLE_LENGTH = 1;

        private static final int MAX_CONTENT_LENGTH = 1_000_000;
        private static final int MIN_CONTENT_LENGTH = 1;

        private static final String CONTENT_PROPERTY_NAME = "content";
        private static final String TITLE_PROPERTY_NAME = "title";

        @Test
        void shouldAccept_OnCorrectArgument() {

            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle().buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldRejectIf_TitleIsTooShort() {

            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withTitle("")
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(TITLE_PROPERTY_NAME);
            });
        }

        @Test
        void shouldRejectIf_TitleIsNull() {

            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withTitle(null)
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(TITLE_PROPERTY_NAME);
            });
        }

        @Test
        void shouldRejectIf_TitleIsTooLong() {

            // given
            int tooLongLength = MAX_TITLE_LENGTH + 1;
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withTitle("x".repeat(tooLongLength))
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(TITLE_PROPERTY_NAME);
            });
        }

        @Test
        void shouldAcceptIf_TitleIsAlmostTooLong() {

            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withTitle("x".repeat(MAX_TITLE_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldRejectIf_ContentIsTooLong() {

            // given
            int tooLongLength = MAX_CONTENT_LENGTH + 1;
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withContent("x".repeat(tooLongLength))
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });
        }

        @Test
        void shouldAcceptIf_ContentIsAlmostTooLong() {

            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withContent("x".repeat(MAX_TITLE_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldRejectIf_ContentIsTooShort() {
            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withContent("")
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });
        }

        @Test
        void shouldRejectIf_ContentIsNull() {
            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withContent(null)
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });
        }

        @Test
        void shouldRejectIf_ContentIsLongEnoughButBlank() {
            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withContent(" ".repeat(MIN_CONTENT_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });
        }

        @Test
        void shouldRejectIf_TitleIsLongEnoughButBlank() {
            // given
            ArticleRequest articleRequest = ArticleTestDataBuilder.anArticle()
                    .withTitle(" ".repeat(MIN_TITLE_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<ArticleRequest>> violations = validator.validate(articleRequest);

            // then
            assertThat(violations).isNotEmpty();

            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(TITLE_PROPERTY_NAME);
            });
        }
    }




    @Nested
    class CommentRequestDTO {

        private static final int MAX_CONTENT_LENGTH = 10_000;
        private static final int MIN_CONTENT_LENGTH = 1;

        private static final String CONTENT_PROPERTY_NAME = "content";

        @Test
        void shouldAccept_OnCorrectArgument() {

            // given
            CommentRequest commentRequest = CommentTestDataBuilder.aComment().buildRequest();

            // when
            Set<ConstraintViolation<CommentRequest>> violations = validator.validate(commentRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldReject_IfContentIsTooShort() {

            // given
            CommentRequest commentRequest = CommentTestDataBuilder.aComment()
                    .withContent("")
                    .buildRequest();

            // when
            Set<ConstraintViolation<CommentRequest>> violations = validator.validate(commentRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });
        }

        @Test
        void shouldReject_IfContentIsNullShort() {

            // given
            CommentRequest commentRequest = CommentTestDataBuilder.aComment()
                    .withContent(null)
                    .buildRequest();

            // when
            Set<ConstraintViolation<CommentRequest>> violations = validator.validate(commentRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });
        }

        @Test
        void shouldReject_IfContentIsTooLong() {

            // given
            CommentRequest commentRequest = CommentTestDataBuilder.aComment()
                    .withContent("x".repeat(MAX_CONTENT_LENGTH + 1))
                    .buildRequest();

            // when
            Set<ConstraintViolation<CommentRequest>> violations = validator.validate(commentRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });
        }

        @Test
        void shouldAccept_IfContentIsAlmostTooLong() {

            // given
            CommentRequest commentRequest = CommentTestDataBuilder.aComment()
                    .withContent("x".repeat(MAX_CONTENT_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<CommentRequest>> violations = validator.validate(commentRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldReject_IfContentIsLongEnoughButBlank() {

            // given
            CommentRequest commentRequest = CommentTestDataBuilder.aComment()
                    .withContent(" ".repeat(MIN_CONTENT_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<CommentRequest>> violations = validator.validate(commentRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(CONTENT_PROPERTY_NAME);
            });        }
    }




    @Nested
    class PersonRequestDTO {

        private static final int MAX_PRIMARY_EMAIL_LENGTH = 50;
        private static final String PRIMARY_EMAIL_PROPERTY_NAME = "primaryEmail";

        @Test
        void shouldAccept_OnCorrectArgument() {

            // given
            PersonRequest personRequest = PersonTestDataBuilder.aPerson().buildRequest();

            // when
            Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldAccept_OnPrimaryEmailAlmostTooLong() {

            // given
            String emailSuffix = "@x.by";
            PersonRequest personRequest = PersonTestDataBuilder.aPerson()
                    .withPrimaryEmail("x".repeat(MAX_PRIMARY_EMAIL_LENGTH - emailSuffix.length()) + emailSuffix)
                    .buildRequest();

            // when
            Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldReject_OnPrimaryEmailTooLong() {

            // given
            String emailSuffix = "@x.by";
            PersonRequest personRequest = PersonTestDataBuilder.aPerson()
                    .withPrimaryEmail("x".repeat(MAX_PRIMARY_EMAIL_LENGTH - emailSuffix.length() + 1) + emailSuffix)
                    .buildRequest();

            // when
            Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(PRIMARY_EMAIL_PROPERTY_NAME);
            });
        }

        @Test
        void shouldReject_IfPrimaryEmailWithoutATSymbol() {

            // given
            PersonRequest personRequest = PersonTestDataBuilder.aPerson()
                    .withPrimaryEmail("xxxx.by")
                    .buildRequest();

            // when
            Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(PRIMARY_EMAIL_PROPERTY_NAME);
            });
        }

        @Test
        void shouldReject_OnPrimaryEmailBlank() {

            // given
            PersonRequest personRequest = PersonTestDataBuilder.aPerson()
                    .withPrimaryEmail("     ")
                    .buildRequest();

            // when
            Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(PRIMARY_EMAIL_PROPERTY_NAME);
            });
        }

    }




    @Nested
    class TopicRequestDTO {

        private final static int MAX_NAME_LENGTH = 50;
        private final static int MIN_NAME_LENGTH = 1;

        private final static String NAME_PROPERTY_NAME = "name";

        @Test
        void shouldAccept_OnCorrectArgument() {

            // given
            TopicRequest topicRequest = TopicTestDataBuilder.aTopic().buildRequest();

            // when
            Set<ConstraintViolation<TopicRequest>> violations = validator.validate(topicRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldAccept_OnNameAlmostTooLong() {

            // given
            TopicRequest topicRequest = TopicTestDataBuilder.aTopic()
                    .withName("x".repeat(MAX_NAME_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<TopicRequest>> violations = validator.validate(topicRequest);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        void shouldReject_OnNameTooLong() {

            // given
            TopicRequest topicRequest = TopicTestDataBuilder.aTopic()
                    .withName("x".repeat(MAX_NAME_LENGTH + 1))
                    .buildRequest();

            // when
            Set<ConstraintViolation<TopicRequest>> violations = validator.validate(topicRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(NAME_PROPERTY_NAME);
            });
        }

        @Test
        void shouldReject_OnNameTooShort() {

            // given
            TopicRequest topicRequest = TopicTestDataBuilder.aTopic()
                    .withName("")
                    .buildRequest();

            // when
            Set<ConstraintViolation<TopicRequest>> violations = validator.validate(topicRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(NAME_PROPERTY_NAME);
            });
        }

        @Test
        void shouldReject_OnNullName() {

            // given
            TopicRequest topicRequest = TopicTestDataBuilder.aTopic()
                    .withName(null)
                    .buildRequest();

            // when
            Set<ConstraintViolation<TopicRequest>> violations = validator.validate(topicRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(NAME_PROPERTY_NAME);
            });
        }

        @Test
        void shouldReject_OnNameLongEnoughButBlank() {

            // given
            TopicRequest topicRequest = TopicTestDataBuilder.aTopic()
                    .withName(" ".repeat(MIN_NAME_LENGTH))
                    .buildRequest();

            // when
            Set<ConstraintViolation<TopicRequest>> violations = validator.validate(topicRequest);

            // then
            assertThat(violations).isNotEmpty();
            violations.forEach(violation ->
            {
                assertThat(violation.getPropertyPath().toString()).isEqualTo(NAME_PROPERTY_NAME);
            });
        }

    }

}
