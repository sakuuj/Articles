package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.annotations.JpaTest;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.entity.jpa.embeddable.ModificationAudit_;
import by.sakuuj.blogsite.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.entity.jpa.entities.TopicEntity_;
import by.sakuuj.blogsite.service.IdempotencyTokenServiceImpl;
import by.sakuuj.testcontainers.PostgresSingletonContainerLauncher;
import by.sakuuj.utils.LocalDateTimeComparator;
import by.sakuuj.utils.PostgresDBCleaner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTest
@Import(IdempotencyTokenServiceImpl.class)
class TopicRepositoryTests extends PostgresSingletonContainerLauncher {

    private final Comparator<LocalDateTime> localDateTimeComparator = LocalDateTimeComparator.getInstance();

    @Autowired
    private TransactionTemplate txTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TopicRepository topicRepository;

    @AfterEach
    void cleanDB() {
        PostgresDBCleaner.truncateTables(txTemplate, entityManager);
    }

    @Nested
    class findById_UUID {

        @Test
        void shouldFind() {

            // given
            TopicEntity topicEntity = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                List<TopicEntity> foundTopics = entityManager
                        .createQuery("SELECT te FROM TopicEntity te", TopicEntity.class)
                        .getResultList();
                assertThat(foundTopics).isEmpty();

                entityManager.persist(topicEntity);
            });

            // when, then
            txTemplate.executeWithoutResult(txStatus -> {
                Optional<TopicEntity> actualOptional = topicRepository.findById(topicEntity.getId());
                assertThat(actualOptional).isNotEmpty();

                assertThat(actualOptional).contains(topicEntity);
            });
        }
    }


    @Nested
    class findAll_Pageable {

        @Test
        void shouldFindAll() {

            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicEntity firstTopicEntity = topicBuilder
                    .withId(null)
                    .withName("first topic")
                    .build();

            TopicEntity secondTopicEntity = topicBuilder
                    .withId(null)
                    .withName("second topic")
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                List<TopicEntity> foundTopics = entityManager
                        .createQuery("SELECT te FROM TopicEntity te", TopicEntity.class)
                        .getResultList();
                assertThat(foundTopics).isEmpty();

                entityManager.persist(firstTopicEntity);
                entityManager.flush();

                entityManager.persist(secondTopicEntity);
            });

            // when, then
            txTemplate.executeWithoutResult(txStatus -> {
                Slice<TopicEntity> actualSlice = topicRepository.findAll(
                        PageRequest.of(
                                0,
                                10,
                                Sort.by(TopicEntity_.MODIFICATION_AUDIT + "." + ModificationAudit_.CREATED_AT)
                        )
                );

                assertThat(actualSlice).containsExactly(firstTopicEntity, secondTopicEntity);
            });
        }
    }

    @Nested
    class removeById_UUID {

        @Test
        void shouldRemoveById() {
            // given
            TopicEntity topicEntity = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                List<TopicEntity> foundTopics = entityManager
                        .createQuery("SELECT te FROM TopicEntity te", TopicEntity.class)
                        .getResultList();
                assertThat(foundTopics).isEmpty();

                entityManager.persist(topicEntity);
            });
            UUID topicId = topicEntity.getId();

            // when
            txTemplate.executeWithoutResult(txStatus -> {
                Optional<TopicEntity> actualOptional = topicRepository.findById(topicId);
                assertThat(actualOptional).contains(topicEntity);

                topicRepository.removeById(topicId);
            });

            // then
            txTemplate.executeWithoutResult(txStatus -> {
                Optional<TopicEntity> actualOptional = topicRepository.findById(topicId);
                assertThat(actualOptional).isEmpty();
            });

        }
    }


    @Nested
    class save_TopicEntity {

        @Test
        void shouldSave() {
            // given
            TopicEntity topicEntity = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                List<TopicEntity> foundTopics = entityManager
                        .createQuery("SELECT te FROM TopicEntity te", TopicEntity.class)
                        .getResultList();
                assertThat(foundTopics).isEmpty();
            });

            // when
            txTemplate.executeWithoutResult(txStatus -> {
                topicRepository.save(topicEntity);
            });

            // then
            txTemplate.executeWithoutResult(txStatus -> {
                Optional<TopicEntity> actualOptional = topicRepository.findById(topicEntity.getId());
                assertThat(actualOptional).isPresent();

                assertThat(actualOptional.get()).usingRecursiveComparison()
                        .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                        .isEqualTo(topicEntity);
            });
        }
    }
}
