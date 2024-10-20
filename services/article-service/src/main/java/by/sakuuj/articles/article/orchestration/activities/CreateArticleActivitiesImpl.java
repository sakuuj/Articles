package by.sakuuj.articles.article.orchestration.activities;

import by.sakuuj.articles.article.dto.ArticleDocumentRequest;
import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.articles.article.exception.IdempotencyTokenExistsException;
import by.sakuuj.articles.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.articles.article.mapper.jpa.ArticleMapper;
import by.sakuuj.articles.article.producer.ElasticsearchEventProducer;
import by.sakuuj.articles.article.repository.jpa.ArticleRepository;
import by.sakuuj.articles.entity.jpa.CreationId;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import by.sakuuj.articles.service.IdempotencyTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateArticleActivitiesImpl implements CreateArticleActivities {

    private final ArticleMapper articleMapper;
    private final ArticleDocumentMapper articleDocumentMapper;

    private final ElasticsearchEventProducer elasticsearchEventProducer;

    private final ArticleRepository articleRepository;

    private final IdempotencyTokenService idempotencyTokenService;

    @Override
    @Transactional
    public ArticleResponse saveInDatabase(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId) {

        idempotencyTokenService.findById(idempotencyTokenId)
                .ifPresent(token -> {
                    throw new IdempotencyTokenExistsException();
                });

        UUID authorId = idempotencyTokenId.getClientId();

        ArticleEntity articleEntityToCreate = articleMapper.toEntity(articleRequest, authorId);
        articleRepository.save(articleEntityToCreate);

        UUID createdArticleId = articleEntityToCreate.getId();
        idempotencyTokenService.create(idempotencyTokenId, CreationId.of(ArticleEntity.class, createdArticleId));

        return articleMapper.toResponse(articleEntityToCreate);
    }

    @Override
    public void sendSaveDocumentEvent(ArticleResponse articleResponse) {

        ArticleDocument articleDocument = articleDocumentMapper.toDocument(articleResponse);
        elasticsearchEventProducer.produce(ArticleDocumentRequest.RequestType.UPSERT, articleDocument);
    }
}
