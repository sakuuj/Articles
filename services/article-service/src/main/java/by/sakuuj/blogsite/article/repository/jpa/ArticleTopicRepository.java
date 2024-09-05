package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.blogsite.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.blogsite.article.repository.jpa.custom.ArticleTopicCustomRepository;
import by.sakuuj.blogsite.entity.jpa.embeddable.ArticleTopicId;
import org.springframework.data.repository.Repository;

public interface ArticleTopicRepository extends Repository<ArticleTopicEntity, ArticleTopicId>,
        ArticleTopicCustomRepository {

    void removeById(ArticleTopicId id);
}
