package by.sakuuj.blogsite.article.mapper.jpa;

import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ToReferenceMapperImpl implements ToReferenceMapper {

    private final EntityManager entityManager;

    @Override
    public PersonEntity getPersonReferenceById(UUID personId) {
        return entityManager.getReference(PersonEntity.class, personId);
    }

    @Override
    public ArticleEntity getArticleReferenceById(UUID articleId) {
        return entityManager.getReference(ArticleEntity.class, articleId);
    }
}
