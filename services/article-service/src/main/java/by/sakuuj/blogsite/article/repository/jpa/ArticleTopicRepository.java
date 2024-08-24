package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.blogsite.article.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.blogsite.article.repository.jpa.custom.ArticleTopicCustomRepository;
import org.springframework.data.repository.Repository;

public interface ArticleTopicRepository extends Repository<ArticleTopicEntity, ArticleTopicId>,
        ArticleTopicCustomRepository {

    void deleteById(ArticleTopicId id);
}
