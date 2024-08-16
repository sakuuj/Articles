package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.entities.jpa.ArticleEntity;
import by.sakuuj.blogplatform.article.entities.jpa.PersonEntity;

import java.util.UUID;

public interface JpaReferenceMapper {

    PersonEntity getPersonReferenceById(UUID personId);

    ArticleEntity getArticleReferenceById(UUID articleId);
}
