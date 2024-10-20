package by.sakuuj.articles.article.repository.jpa.custom;

import by.sakuuj.articles.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.articles.entity.jpa.entities.ArticleTopicEntity;

public interface ArticleTopicCustomRepository {
    ArticleTopicEntity save(ArticleTopicId articleTopicId);
}
