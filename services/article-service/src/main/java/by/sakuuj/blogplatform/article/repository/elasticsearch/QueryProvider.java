package by.sakuuj.blogplatform.article.repository.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.StringQuery;

public interface QueryProvider {
    StringQuery provideQueryToFindIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable);
}