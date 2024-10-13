package by.sakuuj.articles.article.repository.elasticsearch.custom;

import by.sakuuj.articles.paging.PageView;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ArticleDocumentCustomRepository {
    PageView<UUID> findIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable);
}
