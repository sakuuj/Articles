package by.sakuuj.blogsite.article.orchestration.activities;

import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.exception.EntityNotFoundException;
import by.sakuuj.blogsite.article.producer.ElasticsearchEventProducer;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.service.IdempotencyTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteArticleActivitiesImpl implements DeleteArticleActivities {

    private final ElasticsearchEventProducer elasticsearchEventProducer;

    private final ArticleRepository articleRepository;

    private final IdempotencyTokenService idempotencyTokenService;

    @Override
    public void deleteFromDatabase(UUID id) {

        articleRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        articleRepository.deleteById(id);
        idempotencyTokenService.deleteByCreationId(CreationId.of(ArticleEntity.class, id));
    }

    @Override
    public void sendDeleteDocumentEvent(UUID id) {

        elasticsearchEventProducer.produce(ArticleDocumentRequest.RequestType.DELETE, null);
    }
}
