package by.sakuuj.articles.article.repository.jpa;

import by.sakuuj.articles.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.articles.article.repository.jpa.custom.ArticleTopicCustomRepository;
import by.sakuuj.articles.entity.jpa.embeddable.ArticleTopicId;
import org.springframework.data.repository.Repository;

public interface ArticleTopicRepository extends Repository<ArticleTopicEntity, ArticleTopicId>,
        ArticleTopicCustomRepository {

    void removeById(ArticleTopicId id);
}
