package by.sakuuj.articles.article.orchestration.activities;

import by.sakuuj.articles.article.dto.ArticleDocumentRequest;
import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.articles.article.exception.EntityNotFoundException;
import by.sakuuj.articles.article.exception.EntityVersionDoesNotMatch;
import by.sakuuj.articles.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.articles.article.mapper.jpa.ArticleMapper;
import by.sakuuj.articles.article.producer.ElasticsearchEventProducer;
import by.sakuuj.articles.article.repository.jpa.ArticleRepository;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateArticleActivitiesImpl implements UpdateArticleActivities {

    private final ArticleMapper articleMapper;
    private final ArticleDocumentMapper articleDocumentMapper;
    private final TransactionTemplate txTemplate;

    private final ElasticsearchEventProducer elasticsearchEventProducer;

    private final ArticleRepository articleRepository;

    @Override
    public ArticleResponse updateByIdInDatabase(ArticleRequest articleRequest, UUID id, short version) {

        ArticleEntity updatedEntity = txTemplate.execute(txStatus -> {

            ArticleEntity entityToUpdate = articleRepository.findById(id)
                    .orElseThrow(EntityNotFoundException::new);

            if (entityToUpdate.getVersion() != version) {

                throw new EntityVersionDoesNotMatch();
            }

            articleMapper.updateEntity(entityToUpdate, articleRequest);

            return entityToUpdate;
        });

        return articleMapper.toResponse(updatedEntity);
    }

    @Override
    public void sendUpdateDocumentEvent(ArticleResponse articleResponse) {

        ArticleDocument articleDocument = articleDocumentMapper.toDocument(articleResponse);
        elasticsearchEventProducer.produce(ArticleDocumentRequest.RequestType.UPSERT, articleDocument);
    }
}
