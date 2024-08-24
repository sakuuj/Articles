package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.utils.EntityGraphNames;
import by.sakuuj.blogsite.article.repository.jpa.custom.ArticleCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends Repository<ArticleEntity, UUID>, ArticleCustomRepository {

    @EntityGraph(value = EntityGraphNames.ARTICLE_EAGER_WITH_ARTICLE_TOPICS_EAGER)
    Slice<ArticleEntity> findAll(Pageable pageable);

    @EntityGraph(value = EntityGraphNames.ARTICLE_EAGER_WITH_ARTICLE_TOPICS_EAGER)
    Optional<ArticleEntity> findById(UUID id);

    void deleteById(UUID id);

    ArticleEntity save(ArticleEntity article);

    ArticleEntity getReferenceById(UUID id);
}