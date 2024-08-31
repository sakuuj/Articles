package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.annotations.JpaTest;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.entity.jpa.CreationId;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.PersonEntity;
import by.sakuuj.testcontainers.PostgresSingletonContainerLauncher;
import by.sakuuj.utils.LocalDateTimeComparator;
import by.sakuuj.utils.PostgresDBCleaner;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTest
public class IdempotencyTokenRepositoryTests extends PostgresSingletonContainerLauncher {

    private final Comparator<LocalDateTime> localDateTimeComparator = LocalDateTimeComparator.getInstance();

    @Autowired
    private IdempotencyTokenRepository idempotencyTokenRepository;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void cleanDB() {
        PostgresDBCleaner.truncateTables(txTemplate, entityManager);
    }

    @Nested
    class save_IdempotencyTokenEntity {

        @Test
        void shouldSaveIfFullyInitialized() {

            // given
            var foundTokens = txTemplate.execute(txStatus -> entityManager
                    .createQuery("SELECT ite FROM IdempotencyTokenEntity ite", IdempotencyTokenEntity.class)
                    .getResultList()
            );
            assertThat(foundTokens).isEmpty();

            PersonEntity idempotencyTokenOwner = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus ->
                    entityManager.persist(idempotencyTokenOwner)
            );

            UUID clientId = idempotencyTokenOwner.getId();
            var idempotencyTokenValue = UUID.fromString("ed03b605-cc2d-4b9e-89cc-324ce8a38cab");
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            var createdEntityClass = ArticleEntity.class;
            var createdEntityId = UUID.fromString("9f3b2d35-41d3-4b4d-870c-ffe6d9a83508");
            var creationId = CreationId.of(createdEntityClass, createdEntityId);

            var idempotencyTokenToSave = IdempotencyTokenEntity.builder()
                    .id(idempotencyTokenId)
                    .creationId(creationId)
                    .build();

            // when
            txTemplate.executeWithoutResult(txStatus ->
                    idempotencyTokenRepository.save(idempotencyTokenToSave)
            );

            // then
            txTemplate.executeWithoutResult(txStatus -> {
                var foundTokenOrNull = entityManager.find(IdempotencyTokenEntity.class, idempotencyTokenId);
                assertThat(foundTokenOrNull).isNotNull();

                assertThat(foundTokenOrNull).usingRecursiveComparison()
                        .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                        .isEqualTo(idempotencyTokenToSave);
            });
        }
    }

    @Nested
    class removeByCreationId_CreationId {

        @Test
        void shouldRemoveByCreationId() {

            // given
            var foundTokens = txTemplate.execute(txStatus -> entityManager
                    .createQuery("SELECT ite FROM IdempotencyTokenEntity ite", IdempotencyTokenEntity.class)
                    .getResultList()
            );
            assertThat(foundTokens).isEmpty();

            PersonEntity idempotencyTokenOwner = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus ->
                    entityManager.persist(idempotencyTokenOwner)
            );

            UUID clientId = idempotencyTokenOwner.getId();
            var idempotencyTokenValue = UUID.fromString("ed03b605-cc2d-4b9e-89cc-324ce8a38cab");
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            var createdEntityClass = ArticleEntity.class;
            var createdEntityId = UUID.fromString("9f3b2d35-41d3-4b4d-870c-ffe6d9a83508");
            var creationId = CreationId.of(createdEntityClass, createdEntityId);

            var idempotencyTokenToSave = IdempotencyTokenEntity.builder()
                    .id(idempotencyTokenId)
                    .creationId(creationId)
                    .build();

            txTemplate.executeWithoutResult(txStatus ->
                    entityManager.persist(idempotencyTokenToSave)
            );

            // when
            txTemplate.executeWithoutResult(txStatus -> {
                var foundTokenOrNull = entityManager.find(IdempotencyTokenEntity.class, idempotencyTokenId);
                assertThat(foundTokenOrNull).isNotNull();

                idempotencyTokenRepository.removeByCreationId(creationId);
            });

            // then
            txTemplate.executeWithoutResult(txStatus -> {
                var actual = entityManager.find(IdempotencyTokenEntity.class, idempotencyTokenId);
                assertThat(actual).isNull();
            });
        }
    }

    @Nested
    class findById_IdempotencyTokenId {

        @Test
        void shouldFindById() {

            // given
            var foundTokens = txTemplate.execute(txStatus -> entityManager
                    .createQuery("SELECT ite FROM IdempotencyTokenEntity ite", IdempotencyTokenEntity.class)
                    .getResultList()
            );
            assertThat(foundTokens).isEmpty();

            PersonEntity idempotencyTokenOwner = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            txTemplate.executeWithoutResult(txStatus ->
                    entityManager.persist(idempotencyTokenOwner)
            );

            UUID clientId = idempotencyTokenOwner.getId();
            var idempotencyTokenValue = UUID.fromString("ed03b605-cc2d-4b9e-89cc-324ce8a38cab");
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            var createdEntityClass = ArticleEntity.class;
            var createdEntityId = UUID.fromString("9f3b2d35-41d3-4b4d-870c-ffe6d9a83508");
            var creationId = CreationId.of(createdEntityClass, createdEntityId);

            var idempotencyTokenToSave = IdempotencyTokenEntity.builder()
                    .id(idempotencyTokenId)
                    .creationId(creationId)
                    .build();

            txTemplate.executeWithoutResult(txStatus ->
                    entityManager.persist(idempotencyTokenToSave)
            );

            // when, then
            txTemplate.executeWithoutResult(txStatus -> {
                Optional<IdempotencyTokenEntity> actualOptional = idempotencyTokenRepository.findById(idempotencyTokenId);
                assertThat(actualOptional).isNotEmpty();

                assertThat(actualOptional.get()).usingRecursiveComparison()
                        .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                        .isEqualTo(idempotencyTokenToSave);
            });
        }
    }
}
