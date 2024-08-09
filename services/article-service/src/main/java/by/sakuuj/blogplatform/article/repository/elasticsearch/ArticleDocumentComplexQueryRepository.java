package by.sakuuj.blogplatform.article.repository.elasticsearch;

import by.sakuuj.blogplatform.article.repository.PageView;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ArticleDocumentComplexQueryRepository {
    PageView<UUID> findIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable);
}
