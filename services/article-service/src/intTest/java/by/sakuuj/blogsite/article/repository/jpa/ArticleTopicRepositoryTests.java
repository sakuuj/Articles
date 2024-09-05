package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.annotations.JpaTest;
import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.entity.jpa.entities.TopicEntity;
import by.sakuuj.testcontainers.PostgresSingletonContainerLauncher;
import by.sakuuj.utils.PostgresDBCleaner;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTest
public class ArticleTopicRepositoryTests extends PostgresSingletonContainerLauncher {

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ArticleTopicRepository articleTopicRepository;

    @AfterEach
    void cleanDB() {
        PostgresDBCleaner.truncateTables(txTemplate, entityManager);
    }

    @Nested
    class save_ArticleTopicId {

        @Test
        void shouldSave() {

            // given
            PersonEntity personEntity = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleEntity articleEntity = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withAuthor(personEntity)
                    .build();

            TopicEntity topicEntity = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                var foundArticleTopics = entityManager
                        .createQuery("SELECT ate FROM ArticleTopicEntity ate", ArticleTopicEntity.class)
                        .getResultList();
                assertThat(foundArticleTopics).isEmpty();

                entityManager.persist(personEntity);
                entityManager.persist(articleEntity);
                entityManager.persist(topicEntity);
            });

            var articleTopicId = ArticleTopicId.builder()
                    .topicId(topicEntity.getId())
                    .articleId(articleEntity.getId())
                    .build();

            // when
            txTemplate.executeWithoutResult(txStatus ->
                    articleTopicRepository.save(articleTopicId)
            );

            // then
            txTemplate.executeWithoutResult(txStatus -> {
                ArticleTopicEntity actual = entityManager.find(ArticleTopicEntity.class, articleTopicId);
                assertThat(actual).isNotNull();

                assertThat(actual.getId()).isEqualTo(articleTopicId);
            });

        }
    }

    @Nested
    class removeById_ArticleTopicId {

        @Test
        void shouldRemoveById() {

            // given
            PersonEntity personEntity = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleEntity articleEntity = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withAuthor(personEntity)
                    .build();

            TopicEntity topicEntity = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                var foundArticleTopics = entityManager
                        .createQuery("SELECT ate FROM ArticleTopicEntity ate", ArticleTopicEntity.class)
                        .getResultList();
                assertThat(foundArticleTopics).isEmpty();

                entityManager.persist(personEntity);
                entityManager.persist(articleEntity);
                entityManager.persist(topicEntity);
            });

            var articleTopicId = ArticleTopicId.builder()
                    .topicId(topicEntity.getId())
                    .articleId(articleEntity.getId())
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                TopicEntity topicRef = entityManager.getReference(TopicEntity.class, topicEntity.getId());
                ArticleEntity articleRef = entityManager.getReference(ArticleEntity.class, articleEntity.getId());

                ArticleTopicEntity articleTopicEntity = ArticleTopicEntity.builder()
                        .id(articleTopicId)
                        .topic(topicRef)
                        .article(articleRef)
                        .build();

                entityManager.persist(articleTopicEntity);
            });

            // when
            txTemplate.executeWithoutResult(txStatus -> {
                ArticleTopicEntity found = entityManager.find(ArticleTopicEntity.class, articleTopicId);
                assertThat(found).isNotNull();

                articleTopicRepository.removeById(articleTopicId);
            });

            // then
            txTemplate.executeWithoutResult(txStatus -> {
                ArticleTopicEntity actual = entityManager.find(ArticleTopicEntity.class, articleTopicId);
                assertThat(actual).isNull();
            });

        }
    }
}
