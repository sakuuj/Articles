package by.sakuuj.articles.article.mapper.jpa;

import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import by.sakuuj.articles.entity.jpa.entities.PersonEntity;

import java.util.UUID;

public interface ToReferenceMapper {

    PersonEntity getPersonReferenceById(UUID personId);

    ArticleEntity getArticleReferenceById(UUID articleId);
}
