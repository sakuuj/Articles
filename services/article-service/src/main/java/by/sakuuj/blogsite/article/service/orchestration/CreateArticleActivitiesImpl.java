package by.sakuuj.blogsite.article.service.orchestration;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.exception.IdempotencyTokenExistsException;
import by.sakuuj.blogsite.article.exception.NotFoundException;
import by.sakuuj.blogsite.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.blogsite.article.mapper.jpa.ArticleMapper;
import by.sakuuj.blogsite.article.producer.ElasticsearchEventProducer;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.entity.elasticsearch.ArticleDocumentRequest;
import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.service.IdempotencyTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateArticleActivitiesImpl implements CreateArticleActivities {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    private final ElasticsearchEventProducer elasticsearchEventProducer;
    private final ArticleDocumentMapper articleDocumentMapper;

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
    public void sendElasticsearchSaveEvent(UUID articleId) {
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow(NotFoundException::new);

        ArticleDocument documentToSave = articleDocumentMapper.toDocument(articleEntity);
        elasticsearchEventProducer.produce(
                ArticleDocumentRequest.RequestType.UPSERT,
                documentToSave
        );
    }
}
