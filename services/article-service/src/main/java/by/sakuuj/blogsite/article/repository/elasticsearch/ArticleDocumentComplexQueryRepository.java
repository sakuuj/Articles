package by.sakuuj.blogsite.article.repository.elasticsearch;

import by.sakuuj.blogsite.article.paging.PageView;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ArticleDocumentComplexQueryRepository {
    PageView<UUID> findIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable);
}
