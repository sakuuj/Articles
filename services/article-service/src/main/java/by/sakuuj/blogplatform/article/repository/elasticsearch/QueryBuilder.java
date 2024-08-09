package by.sakuuj.blogplatform.article.repository.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.StringQuery;

public interface QueryBuilder {
    StringQuery buildQueryToFindIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable);
}
