package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.entities.jpa.ArticleEntity;
import by.sakuuj.blogplatform.article.entities.jpa.PersonEntity;
import by.sakuuj.blogplatform.article.repository.jpa.ArticleJpaRepository;
import by.sakuuj.blogplatform.article.repository.jpa.PersonJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class JpaReferenceMapperImpl implements JpaReferenceMapper {

    private final PersonJpaRepository personJpaRepository;
    private final ArticleJpaRepository articleJpaRepository;

    public PersonEntity getPersonReferenceById(UUID personId) {
        return personJpaRepository.getReferenceById(personId);
    }

    public ArticleEntity getArticleReferenceById(UUID articleId) {
        return articleJpaRepository.getReferenceById(articleId);
    }
}
