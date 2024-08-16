package by.sakuuj.blogsite.article.mappers;

import by.sakuuj.blogsite.article.entities.jpa.ArticleEntity;
import by.sakuuj.blogsite.article.entities.jpa.PersonEntity;

import java.util.UUID;

public interface JpaReferenceMapper {

    PersonEntity getPersonReferenceById(UUID personId);

    ArticleEntity getArticleReferenceById(UUID articleId);
}
