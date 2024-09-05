package by.sakuuj.blogsite.article.mapper.jpa;

import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;

import java.util.UUID;

public interface ToReferenceMapper {

    PersonEntity getPersonReferenceById(UUID personId);

    ArticleEntity getArticleReferenceById(UUID articleId);
}
