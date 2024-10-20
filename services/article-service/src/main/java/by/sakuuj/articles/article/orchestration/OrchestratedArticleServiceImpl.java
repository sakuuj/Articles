package by.sakuuj.articles.article.orchestration;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.orchestration.workflows.CreateArticleWorkflow;
import by.sakuuj.articles.article.orchestration.workflows.DeleteArticleWorkflow;
import by.sakuuj.articles.article.orchestration.workflows.UpdateArticleWorkflow;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.TemporalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratedArticleServiceImpl implements OrchestratedArticleService {

    private final ObjectFactory<CreateArticleWorkflow> createArticleWorkflow;

    private final ObjectFactory<DeleteArticleWorkflow> deleteArticleWorkflow;

    private final ObjectFactory<UpdateArticleWorkflow> updateArticleWorkflow;


    private static Optional<ApplicationFailure> extractApplicationFailure(TemporalException exception) {

        Throwable t = exception;
        while (t instanceof TemporalException && !(t instanceof ApplicationFailure)) {

            if (t.getCause() != null) {
                t = t.getCause();
            } else {
                return Optional.empty();
            }
        }

        if (t instanceof ApplicationFailure applicationFailure) {
            return Optional.of(applicationFailure);
        }

        return Optional.empty();
    }

    @Override
    public ArticleResponse create(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId) {
        try {
            return createArticleWorkflow.getObject().createArticle(articleRequest, idempotencyTokenId);

        } catch (TemporalException ex) {

            throw extractApplicationFailure(ex).orElseThrow(() -> ex);
        }
    }


    @Override
    public ArticleResponse updateById(ArticleRequest articleRequest, UUID id, short version) {
        try {
            return updateArticleWorkflow.getObject().updateArticle(articleRequest, id, version);

        } catch (TemporalException ex) {

            throw extractApplicationFailure(ex).orElseThrow(() -> ex);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            deleteArticleWorkflow.getObject().deleteDocumentById(id);

        } catch (TemporalException ex) {

            throw extractApplicationFailure(ex).orElseThrow(() -> ex);
        }
    }
}
