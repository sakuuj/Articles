package by.sakuuj.articles.article.orchestration;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.orchestration.workflows.CreateArticleWorkflow;
import by.sakuuj.articles.article.orchestration.workflows.DeleteArticleWorkflow;
import by.sakuuj.articles.article.orchestration.workflows.UpdateArticleWorkflow;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestratedArticleServiceImpl implements OrchestratedArticleService {

    private final ObjectFactory<CreateArticleWorkflow> createArticleWorkflow;

    private final ObjectFactory<DeleteArticleWorkflow> deleteArticleWorkflow;

    private final ObjectFactory<UpdateArticleWorkflow> updateArticleWorkflow;

    @Override
    public ArticleResponse create(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId) {

        return createArticleWorkflow.getObject().createArticle(articleRequest, idempotencyTokenId);
    }

    @Override
    public ArticleResponse updateById(ArticleRequest articleRequest, UUID id, short version) {

        return updateArticleWorkflow.getObject().updateArticle(articleRequest, id, version);
    }

    @Override
    public void deleteById(UUID id) {

        deleteArticleWorkflow.getObject().deleteDocumentById(id);
    }
}
