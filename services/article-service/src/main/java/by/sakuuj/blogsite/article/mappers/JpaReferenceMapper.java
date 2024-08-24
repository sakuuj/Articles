package by.sakuuj.blogsite.article.mappers;

import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.PersonEntity;

import java.util.UUID;

public interface JpaReferenceMapper {

    PersonEntity getPersonReferenceById(UUID personId);

    ArticleEntity getArticleReferenceById(UUID articleId);
}
