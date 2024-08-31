package by.sakuuj.blogsite.article.repository.elasticsearch.cutsom;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.StringQuery;

public interface QueryProvider {
    StringQuery provideQueryToFindIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable);
}