package by.sakuuj.blogsite.article.repository.jpa.custom;

import by.sakuuj.blogsite.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleTopicEntity;

public interface ArticleTopicCustomRepository {
    ArticleTopicEntity save(ArticleTopicId articleTopicId);
}
