package by.sakuuj.blogsite.article.repository.jpa.custom;

import by.sakuuj.blogsite.article.entity.jpa.embeddable.ArticleTopicId_;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.ModificationAudit_;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity_;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleTopicEntity_;
import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity_;
import by.sakuuj.blogsite.article.entity.jpa.utils.EntityGraphNames;
import by.sakuuj.blogsite.article.paging.PageView;
import by.sakuuj.blogsite.article.paging.RequestedPage;
import by.sakuuj.blogsite.article.repository.jpa.custom.utils.CriteriaUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaEntityJoin;
import org.hibernate.query.criteria.JpaRoot;
import org.hibernate.query.criteria.JpaSubQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {

    private final EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<ArticleEntity> findAllByIdsInOrder(List<UUID> ids) {

        Session session = entityManager.unwrap(Session.class);
        RootGraph<ArticleEntity> entityGraph = (RootGraph<ArticleEntity>) session
                .getEntityGraph(EntityGraphNames.ARTICLE_EAGER_WITH_ARTICLE_TOPICS_EAGER);

        return session
                .byMultipleIds(ArticleEntity.class)
                .withFetchGraph(entityGraph)
                .enableOrderedReturn(true)
                .multiLoad(ids);
    }

    /*
    { List<String> topicNames; } -> context

    Expecting output (w/o entity graph):

    SELECT a FROM articles a
    WHERE a.article_id IN ((
                SELECT at.article_id
                FROM article_topics at
                JOIN topics t
                    ON at.topic_id = t.topic_id
                WHERE t.name IN ( { topicNames } )
                GROUP BY at.article_id
                HAVING COUNT(*) = { topicNames.size() }
                ))
     ORDER BY a.created_at DESC
     OFFSET ?
     FETCH FIRST ? ROWS ONLY;
     */
    @Override
    @SuppressWarnings("unchecked")
    public PageView<ArticleEntity> findAllByTopicsAndSortByCreatedAtDesc(List<String> topicNames, RequestedPage requestedPage) {

        Session session = entityManager.unwrap(Session.class);

        HibernateCriteriaBuilder builder = session.getCriteriaBuilder();

        JpaCriteriaQuery<ArticleEntity> query = builder.createQuery(ArticleEntity.class);
        initializeQueryToFindAllByTopicsSortedByDateDesc(topicNames, query, builder);

        Query<ArticleEntity> createdQuery = session.createQuery(query);
        RootGraph<ArticleEntity> entityGraph = (RootGraph<ArticleEntity>) session
                .getEntityGraph(EntityGraphNames.ARTICLE_EAGER_WITH_ARTICLE_TOPICS_EAGER);
        createdQuery.setEntityGraph(entityGraph, GraphSemantic.FETCH);

        return CriteriaUtils.getPagedQueryResult(requestedPage, createdQuery);
    }

    private static void initializeQueryToFindAllByTopicsSortedByDateDesc(List<String> topicNames,
                                                                         JpaCriteriaQuery<ArticleEntity> query,
                                                                         HibernateCriteriaBuilder builder) {
        JpaRoot<ArticleEntity> root = query.from(ArticleEntity.class);

        JpaSubQuery<UUID> sq = query.subquery(UUID.class);

        initializeSubqueryToFindArticleIdsOfArticlesWithTopics(topicNames, builder, sq);
        query.where(root.get(ArticleEntity_.id).in(sq));

        query.select(root);

        query.orderBy(
                builder.desc(
                        root.get(ArticleEntity_.modificationAudit)
                                .get(ModificationAudit_.createdAt)
                )
        );
    }

    private static void initializeSubqueryToFindArticleIdsOfArticlesWithTopics(List<String> topicNames,
                                                                               HibernateCriteriaBuilder builder,
                                                                               JpaSubQuery<UUID> sq) {
        JpaRoot<ArticleTopicEntity> sqRoot = sq.from(ArticleTopicEntity.class);

        JpaEntityJoin<TopicEntity> sqJoin = sqRoot.join(TopicEntity.class);
        sqJoin.on(
                builder.equal(
                        sqRoot.get(ArticleTopicEntity_.id).get(ArticleTopicId_.topicId),
                        sqJoin.get(TopicEntity_.id)
                )
        );

        sq.where(sqJoin.get(TopicEntity_.name).in(topicNames));

        sq.groupBy(
                sqRoot.get(ArticleTopicEntity_.id)
                        .get(ArticleTopicId_.articleId)
        );

        sq.having(builder.equal(builder.count(), topicNames.size()));

        sq.select(sqRoot
                .get(ArticleTopicEntity_.id)
                .get(ArticleTopicId_.articleId)
        );
    }
}
