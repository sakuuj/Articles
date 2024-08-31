package by.sakuuj.blogsite.article.repository.elasticsearch.cutsom;

import by.sakuuj.blogsite.article.paging.PageView;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ArticleDocumentCustomRepository {
    PageView<UUID> findIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable);
}
