package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.annotations.JpaTest;
import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.ModificationAudit;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.ModificationAudit_;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity_;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.article.paging.PageView;
import by.sakuuj.blogsite.article.paging.RequestedPage;
import by.sakuuj.testcontainers.PostgresSingletonContainerLauncher;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTest
public class ArticleRepositoryTests extends PostgresSingletonContainerLauncher {

    private static final Comparator<LocalDateTime> localDateTimeComparator = (o1, o2) ->
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS");

        String o1Formatted = o1.format(dateTimeFormatter);
        String o2Formatted = o2.format(dateTimeFormatter);
        return CharSequence.compare(o1Formatted, o2Formatted);
    };

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void cleanDB() {
        txTemplate.executeWithoutResult(txStatus -> {
            entityManager.createNativeQuery(
                    """
                            DO
                            $$BEGIN
                            EXECUTE  'TRUNCATE TABLE ' ||
                            (SELECT array_to_string(
                                (
                                SELECT ARRAY(SELECT table_name FROM information_schema.tables 
                                    WHERE table_schema = 'public' 
                                    AND table_name NOT LIKE 'databasechangelog%')
                                ), ','
                            ))::VARCHAR;
                            END$$
                            """).executeUpdate();
        });
    }

    @Nested
    class shouldFindById_UUID {

        @Test
        void shouldFind_WithInitializedAuthor_AndArticleTopics() {

            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleEntity article = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withTopics(null)
                    .withAuthor(author)
                    .build();

            TopicEntity topic = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            ArticleTopicEntity articleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(article.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            txTemplate.executeWithoutResult(txStatus ->
            {
                entityManager.persist(author);
                entityManager.persist(article);
                entityManager.persist(topic);

                articleTopic.setArticle(entityManager.getReference(ArticleEntity.class, article.getId()));
                articleTopic.setTopic(entityManager.getReference(TopicEntity.class, topic.getId()));
                entityManager.persist(articleTopic);
            });

            // when
            Optional<ArticleEntity> actualOptional = articleRepository.findById(article.getId());

            // then
            assertThat(actualOptional).isPresent();
            ArticleEntity actual = actualOptional.get();

            assertThat(Hibernate.isInitialized(actual.getAuthor())).isTrue();
            assertThat(Hibernate.isInitialized(actual.getArticleTopics())).isTrue();

            assertThat(actual.getAuthor()).isEqualTo(author);
            assertThat(actual.getArticleTopics().getFirst()).isEqualTo(articleTopic);

            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields(
                            ArticleEntity_.AUTHOR,
                            ArticleEntity_.ARTICLE_TOPICS
                    )
                    .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                    .isEqualTo(article);
        }
    }

    @Nested
    class shouldFindAll_Pageable {

        @Test
        void shouldFind_WithInitializedAuthor_AndArticleTopics() {

            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleTestDataBuilder articleTestDataBuilder = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withTopics(null)
                    .withAuthor(author);

            ArticleEntity firstArticle = articleTestDataBuilder
                    .withTitle("first title")
                    .build();

            ArticleEntity secondArticle = articleTestDataBuilder
                    .withTitle("second title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            TopicEntity topic = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            ArticleTopicEntity firstArticleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(firstArticle.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity secondArticleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(secondArticle.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            txTemplate.executeWithoutResult(txStatus ->
            {
                entityManager.persist(author);
                entityManager.persist(firstArticle);
                entityManager.persist(topic);

                firstArticleTopic.setArticle(entityManager.getReference(ArticleEntity.class, firstArticle.getId()));
                firstArticleTopic.setTopic(entityManager.getReference(TopicEntity.class, topic.getId()));
                entityManager.persist(firstArticleTopic);

                entityManager.flush();

                entityManager.persist(secondArticle);

                secondArticleTopic.setArticle(entityManager.getReference(ArticleEntity.class, secondArticle.getId()));
                secondArticleTopic.setTopic(entityManager.getReference(TopicEntity.class, topic.getId()));
                entityManager.persist(secondArticleTopic);
            });

            // when
            Slice<ArticleEntity> actualSlice = articleRepository.findAll(
                    PageRequest.of(0, 10,
                            Sort.by(Sort.Direction.ASC,
                                    ArticleEntity_.MODIFICATION_AUDIT + "." + ModificationAudit_.CREATED_AT))
            );

            // then
            assertThat(actualSlice.getNumberOfElements()).isEqualTo(2);

            ArticleEntity foundFirstArticle = actualSlice.getContent().get(0);
            assertThat(Hibernate.isInitialized(foundFirstArticle.getAuthor())).isTrue();
            assertThat(Hibernate.isInitialized(foundFirstArticle.getArticleTopics())).isTrue();

            assertThat(foundFirstArticle.getAuthor()).isEqualTo(author);
            assertThat(foundFirstArticle.getArticleTopics().getFirst()).isEqualTo(firstArticleTopic);

            assertThat(foundFirstArticle).usingRecursiveComparison()
                    .ignoringFields(
                            ArticleEntity_.AUTHOR,
                            ArticleEntity_.ARTICLE_TOPICS
                    )
                    .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                    .isEqualTo(firstArticle);


            ArticleEntity foundSecondArticle = actualSlice.getContent().get(1);
            assertThat(Hibernate.isInitialized(foundSecondArticle.getAuthor())).isTrue();
            assertThat(Hibernate.isInitialized(foundSecondArticle.getArticleTopics())).isTrue();

            assertThat(foundSecondArticle.getAuthor()).isEqualTo(author);
            assertThat(foundSecondArticle.getArticleTopics().getFirst()).isEqualTo(secondArticleTopic);

            assertThat(foundSecondArticle).usingRecursiveComparison()
                    .ignoringFields(
                            ArticleEntity_.AUTHOR,
                            ArticleEntity_.ARTICLE_TOPICS
                    )
                    .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                    .isEqualTo(secondArticle);
        }

        @Test
        void shouldFindCorrectPage() {

            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleTestDataBuilder articleTestDataBuilder = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withTopics(null)
                    .withAuthor(author);

            ArticleEntity firstArticle = articleTestDataBuilder
                    .withTitle("first title")
                    .build();

            ArticleEntity secondArticle = articleTestDataBuilder
                    .withTitle("second title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            ArticleEntity thirdArticle = articleTestDataBuilder
                    .withTitle("third title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            txTemplate.executeWithoutResult(txStatus ->
            {
                entityManager.persist(author);
                entityManager.persist(firstArticle);
                entityManager.flush();
                entityManager.persist(secondArticle);
                entityManager.flush();
                entityManager.persist(thirdArticle);
            });

            int pageSize = 1;

            // when
            Sort sortByCreatedAtAsc = Sort.by(Sort.Direction.ASC,
                    ArticleEntity_.MODIFICATION_AUDIT + "." + ModificationAudit_.CREATED_AT);
            Slice<ArticleEntity> zeroSlice = articleRepository.findAll(
                    PageRequest.of(0, pageSize, sortByCreatedAtAsc)
            );

            Slice<ArticleEntity> firstSlice = articleRepository.findAll(
                    PageRequest.of(1, pageSize, sortByCreatedAtAsc)
            );

            Slice<ArticleEntity> secondSlice = articleRepository.findAll(
                    PageRequest.of(2, pageSize, sortByCreatedAtAsc)
            );

            Slice<ArticleEntity> thirdSlice = articleRepository.findAll(
                    PageRequest.of(3, pageSize, sortByCreatedAtAsc)
            );


            // then
            assertThat(zeroSlice.getNumberOfElements()).isEqualTo(1);
            assertThat(zeroSlice.getContent().getFirst()).isEqualTo(firstArticle);

            assertThat(firstSlice.getNumberOfElements()).isEqualTo(1);
            assertThat(firstSlice.getContent().getFirst()).isEqualTo(secondArticle);


            assertThat(secondSlice.getNumberOfElements()).isEqualTo(1);
            assertThat(secondSlice.getContent().getFirst()).isEqualTo(thirdArticle);

            assertThat(thirdSlice.getNumberOfElements()).isZero();
        }
    }


    @Nested
    class shouldFindAllByIdsInOrder_List$UUID$ {

        @ParameterizedTest
        @ValueSource(booleans = {false, true})
        void shouldFind_WithInitializedAuthor_AndArticleTopics(boolean requestListReversed) {

            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleTestDataBuilder articleTestDataBuilder = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withTopics(null)
                    .withAuthor(author);

            ArticleEntity firstArticle = articleTestDataBuilder
                    .withTitle("first title")
                    .build();

            ArticleEntity secondArticle = articleTestDataBuilder
                    .withTitle("second title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            TopicEntity topic = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            ArticleTopicEntity firstArticleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(firstArticle.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity secondArticleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(secondArticle.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            txTemplate.executeWithoutResult(txStatus ->
            {
                entityManager.persist(author);
                entityManager.persist(firstArticle);
                entityManager.persist(secondArticle);
                entityManager.persist(topic);

                firstArticleTopic.setArticle(entityManager.getReference(ArticleEntity.class, firstArticle.getId()));
                firstArticleTopic.setTopic(entityManager.getReference(TopicEntity.class, topic.getId()));
                entityManager.persist(firstArticleTopic);

                secondArticleTopic.setArticle(entityManager.getReference(ArticleEntity.class, secondArticle.getId()));
                secondArticleTopic.setTopic(entityManager.getReference(TopicEntity.class, topic.getId()));
                entityManager.persist(secondArticleTopic);
            });

            List<UUID> requestedIds = new ArrayList<>(List.of(firstArticle.getId(), secondArticle.getId()));
            if (requestListReversed) {
                Collections.reverse(requestedIds);
            }
            // when
            List<ArticleEntity> foundArticles = txTemplate.execute(txStatus ->
                    articleRepository.findAllByIdsInOrder(requestedIds)
            );


            // then
            assertThat(foundArticles.size()).isEqualTo(2);

            ArticleEntity foundFirstArticle = foundArticles.get(requestListReversed ? 1 : 0);
            assertThat(Hibernate.isInitialized(foundFirstArticle.getAuthor())).isTrue();
            assertThat(Hibernate.isInitialized(foundFirstArticle.getArticleTopics())).isTrue();

            assertThat(foundFirstArticle.getAuthor()).isEqualTo(author);
            assertThat(foundFirstArticle.getArticleTopics().getFirst()).isEqualTo(firstArticleTopic);

            assertThat(foundFirstArticle).usingRecursiveComparison()
                    .ignoringFields(
                            ArticleEntity_.AUTHOR,
                            ArticleEntity_.ARTICLE_TOPICS
                    )
                    .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                    .isEqualTo(firstArticle);


            ArticleEntity foundSecondArticle = foundArticles.get(requestListReversed ? 0 : 1);
            assertThat(Hibernate.isInitialized(foundSecondArticle.getAuthor())).isTrue();
            assertThat(Hibernate.isInitialized(foundSecondArticle.getArticleTopics())).isTrue();

            assertThat(foundSecondArticle.getAuthor()).isEqualTo(author);
            assertThat(foundSecondArticle.getArticleTopics().getFirst()).isEqualTo(secondArticleTopic);

            assertThat(foundSecondArticle).usingRecursiveComparison()
                    .ignoringFields(
                            ArticleEntity_.AUTHOR,
                            ArticleEntity_.ARTICLE_TOPICS
                    )
                    .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                    .isEqualTo(secondArticle);
        }
    }

    @Nested
    class findAllByTopicsSortedByCreatedAtDesc_List$String$_RequestedPage {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3})
        void shouldFind_WithInitializedAuthor_AndArticleTopics(int numberOfArticlesWithBothTopics) {
            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleTestDataBuilder articleTestDataBuilder = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withTopics(null)
                    .withAuthor(author);

            ArticleEntity firstArticle = articleTestDataBuilder
                    .withTitle("first title")
                    .build();

            ArticleEntity secondArticle = articleTestDataBuilder
                    .withTitle("second title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            ArticleEntity thirdArticle = articleTestDataBuilder
                    .withTitle("third title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            String firstTopicName = "first topic name";
            TopicEntity firstTopic = TopicTestDataBuilder.aTopic()
                    .withName(firstTopicName)
                    .withId(null)
                    .build();

            String secondTopicName = "second topic name";
            TopicEntity secondTopic = TopicTestDataBuilder.aTopic()
                    .withName(secondTopicName)
                    .withId(null)
                    .build();

            ArticleTopicEntity firstArticleFirstTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(firstArticle.getId())
                                    .topicId(firstTopic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity secondArticleFirstTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(secondArticle.getId())
                                    .topicId(firstTopic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity thirdArticleFirstTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(thirdArticle.getId())
                                    .topicId(firstTopic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity firstArticleSecondTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(firstArticle.getId())
                                    .topicId(secondTopic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity secondArticleSecondTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(secondArticle.getId())
                                    .topicId(secondTopic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity thirdArticleSecondTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(thirdArticle.getId())
                                    .topicId(secondTopic.getId())
                                    .build()
                    ).build();


            List<ArticleTopicEntity> firstArticleArticleTopics = List.of(firstArticleFirstTopic, firstArticleSecondTopic);
            List<ArticleTopicEntity> secondArticleArticleTopics = List.of(secondArticleFirstTopic, secondArticleSecondTopic);
            List<ArticleTopicEntity> thirdArticleArticleTopics = List.of(thirdArticleFirstTopic, thirdArticleSecondTopic);

            List<List<ArticleTopicEntity>> allArticleArticleTopics = List.of(
                    firstArticleArticleTopics,
                    secondArticleArticleTopics,
                    thirdArticleArticleTopics
            );

            List<ArticleEntity> articles = List.of(
                    firstArticle,
                    secondArticle,
                    thirdArticle
            );

            txTemplate.executeWithoutResult(txStatus ->
            {
                entityManager.persist(author);
                entityManager.persist(firstTopic);
                entityManager.persist(secondTopic);

                entityManager.persist(thirdArticle);
                entityManager.flush();
                entityManager.persist(secondArticle);
                entityManager.flush();
                entityManager.persist(firstArticle);

                for (int i = 0; i < 3; i++) {
                    ArticleTopicEntity articleFirstTopic = allArticleArticleTopics.get(i).get(0);
                    articleFirstTopic.setArticle(entityManager.getReference(ArticleEntity.class, articles.get(i).getId()));
                    articleFirstTopic.setTopic(entityManager.getReference(TopicEntity.class, firstTopic.getId()));
                    entityManager.persist(articleFirstTopic);
                }

                for (int i = 0; i < numberOfArticlesWithBothTopics; i++) {
                    ArticleTopicEntity articleSecondTopic = allArticleArticleTopics.get(i).get(1);
                    articleSecondTopic.setArticle(entityManager.getReference(ArticleEntity.class, articles.get(i).getId()));
                    articleSecondTopic.setTopic(entityManager.getReference(TopicEntity.class, secondTopic.getId()));
                    entityManager.persist(articleSecondTopic);
                }
            });

            RequestedPage requestedPage = RequestedPage.aPage().withSize(10);

            // when
            PageView<ArticleEntity> foundArticles = txTemplate.execute(txStatus ->
                    articleRepository.findAllByTopicsAndSortByCreatedAtDesc(
                            List.of(firstTopicName, secondTopicName), requestedPage
                    )
            );

            // then
            assertThat(foundArticles.content().size()).isEqualTo(numberOfArticlesWithBothTopics);

            for (int i = 0; i < numberOfArticlesWithBothTopics; i++) {
                ArticleEntity foundArticle = foundArticles.content().get(i);
                assertThat(Hibernate.isInitialized(foundArticle.getAuthor())).isTrue();
                assertThat(Hibernate.isInitialized(foundArticle.getArticleTopics())).isTrue();

                assertThat(foundArticle.getAuthor()).isEqualTo(author);
                assertThat(foundArticle.getArticleTopics()).containsAll(allArticleArticleTopics.get(i));

                assertThat(foundArticle).usingRecursiveComparison()
                        .ignoringFields(
                                ArticleEntity_.AUTHOR,
                                ArticleEntity_.ARTICLE_TOPICS
                        )
                        .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                        .isEqualTo(articles.get(i));
            }
        }

        @Test
        void shouldFindCorrectPage() {

            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleTestDataBuilder articleTestDataBuilder = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withTopics(null)
                    .withAuthor(author);

            ArticleEntity firstArticle = articleTestDataBuilder
                    .withTitle("first title")
                    .build();

            ArticleEntity secondArticle = articleTestDataBuilder
                    .withTitle("second title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            ArticleEntity thirdArticle = articleTestDataBuilder
                    .withTitle("third title")
                    .withModificationAudit(new ModificationAudit())
                    .build();

            TopicEntity topic = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            ArticleTopicEntity firstArticleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(firstArticle.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity secondArticleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(secondArticle.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            ArticleTopicEntity thirdArticleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(thirdArticle.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            txTemplate.executeWithoutResult(txStatus ->
            {
                entityManager.persist(topic);
                entityManager.persist(author);
                entityManager.persist(thirdArticle);
                entityManager.flush();
                entityManager.persist(secondArticle);
                entityManager.flush();
                entityManager.persist(firstArticle);

                firstArticleTopic.setArticle(firstArticle);
                firstArticleTopic.setTopic(topic);

                secondArticleTopic.setArticle(secondArticle);
                secondArticleTopic.setTopic(topic);

                thirdArticleTopic.setArticle(thirdArticle);
                thirdArticleTopic.setTopic(topic);

                entityManager.persist(firstArticleTopic);
                entityManager.persist(secondArticleTopic);
                entityManager.persist(thirdArticleTopic);
            });

            int pageSize = 1;

            // when

            PageView<ArticleEntity> zeroPage = articleRepository.findAllByTopicsAndSortByCreatedAtDesc(
                    List.of(topic.getName()), RequestedPage.aPage().withNumber(0).withSize(pageSize)
            );

            PageView<ArticleEntity> firstPage = articleRepository.findAllByTopicsAndSortByCreatedAtDesc(
                    List.of(topic.getName()), RequestedPage.aPage().withNumber(1).withSize(pageSize)
            );

            PageView<ArticleEntity> secondPage = articleRepository.findAllByTopicsAndSortByCreatedAtDesc(
                    List.of(topic.getName()), RequestedPage.aPage().withNumber(2).withSize(pageSize)
            );

            PageView<ArticleEntity> thirdPage = articleRepository.findAllByTopicsAndSortByCreatedAtDesc(
                    List.of(topic.getName()), RequestedPage.aPage().withNumber(3).withSize(pageSize)
            );


            // then
            assertThat(zeroPage.content().size()).isEqualTo(1);
            assertThat(zeroPage.content().getFirst()).isEqualTo(firstArticle);

            assertThat(firstPage.content().size()).isEqualTo(1);
            assertThat(firstPage.content().getFirst()).isEqualTo(secondArticle);

            assertThat(secondPage.content().size()).isEqualTo(1);
            assertThat(secondPage.content().getFirst()).isEqualTo(thirdArticle);

            assertThat(thirdPage.content().size()).isZero();
        }
    }


    @Nested
    class deleteById_UUID {

        @Test
        void shouldDelete() {
            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleEntity article = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withTopics(null)
                    .withAuthor(author)
                    .build();

            TopicEntity topic = TopicTestDataBuilder.aTopic()
                    .withId(null)
                    .build();

            ArticleTopicEntity articleTopic = ArticleTopicEntity.builder()
                    .id(
                            ArticleTopicId.builder()
                                    .articleId(article.getId())
                                    .topicId(topic.getId())
                                    .build()
                    ).build();

            txTemplate.executeWithoutResult(txStatus ->
            {
                entityManager.persist(author);
                entityManager.persist(article);
                entityManager.persist(topic);

                articleTopic.setArticle(entityManager.getReference(ArticleEntity.class, article.getId()));
                articleTopic.setTopic(entityManager.getReference(TopicEntity.class, topic.getId()));
                entityManager.persist(articleTopic);
            });

            txTemplate.executeWithoutResult(txStatus -> {
                ArticleEntity articleEntity = entityManager.find(ArticleEntity.class, article.getId());

                assertThat(articleEntity).isNotNull();
            });

            articleRepository.deleteById(article.getId());

            txTemplate.executeWithoutResult(txStatus -> {
                ArticleEntity articleEntity = entityManager.find(ArticleEntity.class, article.getId());

                assertThat(articleEntity).isNull();
            });
        }
    }

    @Nested
    class save_ArticleEntity {

        @Test
        void shouldUpdate() {
            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            String oldArticleTitle = "old title";
            String oldArticleContent = "old content";

            String newArticleTitle = "new title";
            String newArticleContent = "new content";

            ArticleEntity article = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                    .withArticleTopics(List.of())
                    .withAuthor(author)
                    .withTitle(oldArticleTitle)
                    .withContent(oldArticleContent)
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                entityManager.persist(author);
                entityManager.persist(article);
            });

            txTemplate.executeWithoutResult(txStatus -> {
                ArticleEntity insertedArticle = entityManager.find(ArticleEntity.class, article.getId());
                assertThat(insertedArticle).isNotNull();
                assertThat(insertedArticle.getAuthor()).isEqualTo(author);
                assertThat(insertedArticle)
                        .usingRecursiveComparison()
                        .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                        .ignoringFields(ArticleEntity_.AUTHOR)
                        .isEqualTo(article);

                insertedArticle.setContent(newArticleContent);
                insertedArticle.setTitle(newArticleTitle);
            });

            txTemplate.executeWithoutResult(txStatus -> {
                ArticleEntity updatedArticle = entityManager.find(ArticleEntity.class, article.getId());

                assertThat(updatedArticle).isNotNull();
                assertThat(updatedArticle.getAuthor()).isEqualTo(author);

                assertThat(updatedArticle.getTitle()).isEqualTo(newArticleTitle);
                assertThat(updatedArticle.getContent()).isEqualTo(newArticleContent);

                assertThat(updatedArticle.getModificationAudit().getUpdatedAt())
                        .usingComparator(localDateTimeComparator)
                        .isAfter(article.getModificationAudit().getUpdatedAt());

                assertThat(updatedArticle.getModificationAudit().getCreatedAt())
                        .usingComparator(localDateTimeComparator)
                        .isEqualTo(article.getModificationAudit().getCreatedAt());
            });
        }

        @Test
        void shouldInsert() {
            PersonEntity author = PersonTestDataBuilder.aPerson()
                    .withId(null)
                    .build();

            ArticleEntity article = ArticleTestDataBuilder.anArticle()
                    .withId(null)
                      .withArticleTopics(List.of())
                    .withAuthor(author)
                    .build();

            txTemplate.executeWithoutResult(txStatus -> {
                entityManager.persist(author);
            });

            txTemplate.executeWithoutResult(txStatus -> {
                List<ArticleEntity> articles = entityManager
                        .createQuery("SELECT ae FROM ArticleEntity ae", ArticleEntity.class)
                        .getResultList();

                assertThat(articles).isEmpty();
            });

            txTemplate.executeWithoutResult(txStatus -> {
                PersonEntity authorReference = entityManager.getReference(PersonEntity.class, author.getId());

                article.setAuthor(authorReference);

                articleRepository.save(article);
            });

            txTemplate.executeWithoutResult(txStatus -> {
                ArticleEntity insertedArticle = entityManager.find(ArticleEntity.class, article.getId());

                assertThat(insertedArticle).isNotNull();
                assertThat(insertedArticle.getAuthor()).isEqualTo(author);
                assertThat(insertedArticle)
                        .usingRecursiveComparison()
                        .withComparatorForType(localDateTimeComparator, LocalDateTime.class)
                        .ignoringFields(ArticleEntity_.AUTHOR)
                        .isEqualTo(article);
            });
        }
    }


    @Nested
    class getReferenceById_UUID {

        @Test
        void shouldGetReference() {
            // given
            UUID uuid = ArticleTestDataBuilder.anArticle().getId();

            // when, then
            txTemplate.executeWithoutResult(txStatus -> {
                ArticleEntity reference = articleRepository.getReferenceById(uuid);

                assertThat(Hibernate.isInitialized(reference)).isFalse();
            });
        }
    }


}
