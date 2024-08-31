package by.sakuuj.blogsite.article.mapper.jpa;

import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.article.repository.jpa.PersonJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ToReferenceMapperImpl implements ToReferenceMapper {

    private final PersonJpaRepository personJpaRepository;
    private final ArticleRepository articleRepository;

    public PersonEntity getPersonReferenceById(UUID personId) {
        return personJpaRepository.getReferenceById(personId);
    }

    public ArticleEntity getArticleReferenceById(UUID articleId) {
        return articleRepository.getReferenceById(articleId);
    }
}
