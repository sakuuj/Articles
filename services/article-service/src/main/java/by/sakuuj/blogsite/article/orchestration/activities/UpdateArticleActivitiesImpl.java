package by.sakuuj.blogsite.article.orchestration.activities;

import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.exception.EntityNotFoundException;
import by.sakuuj.blogsite.article.exception.EntityVersionDoesNotMatch;
import by.sakuuj.blogsite.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.blogsite.article.mapper.jpa.ArticleMapper;
import by.sakuuj.blogsite.article.producer.ElasticsearchEventProducer;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
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
