package by.sakuuj.blogplatform.article.repository.elasticsearch;

import by.sakuuj.blogplatform.article.repository.PageView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.function.Function;

public interface SearchHitsToPageViewMapper {
    <T,R> PageView<R> map(SearchHits<T> sourceHits, Pageable pageable, Function<T, R> typeMapper);
}
