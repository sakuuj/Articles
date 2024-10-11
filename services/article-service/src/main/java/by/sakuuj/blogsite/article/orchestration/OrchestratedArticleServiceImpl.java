package by.sakuuj.blogsite.article.orchestration;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.orchestration.workflows.CreateArticleWorkflow;
import by.sakuuj.blogsite.article.orchestration.workflows.DeleteArticleWorkflow;
import by.sakuuj.blogsite.article.orchestration.workflows.UpdateArticleWorkflow;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestratedArticleServiceImpl implements OrchestratedArticleService {

    private final CreateArticleWorkflow createArticleWorkflow;

    private final DeleteArticleWorkflow deleteArticleWorkflow;

    private final UpdateArticleWorkflow updateArticleWorkflow;

    @Override
    public ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId) {

        return createArticleWorkflow.createArticle(articleRequest, idempotencyTokenId);
    }

    @Override
    public ArticleResponse updateArticle(ArticleRequest articleRequest, UUID id, short version) {

        return updateArticleWorkflow.updateArticle(articleRequest, id, version);
    }

    @Override
    public void deleteDocumentById(UUID id) {

        deleteArticleWorkflow.deleteDocumentById(id);
    }
}
