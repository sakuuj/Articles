package by.sakuuj.blogsite.article.repository.elasticsearch.cutsom;

import by.sakuuj.blogsite.article.paging.PageView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SearchHitsToPageViewMapperImpl implements SearchHitsToPageViewMapper {

    @Override
    public <T,R> PageView<R> map(SearchHits<T> searchHits, Pageable pageable, Function<T, R> typeMapper) {
        Objects.requireNonNull(searchHits);

        SearchPage<T> searchedPage = SearchHitSupport.searchPageFor(searchHits, pageable);

        List<R> mappedContent = searchHits.stream()
                .map(SearchHit::getContent)
                .map(typeMapper)
                .collect(Collectors.toCollection(ArrayList::new));

        return new PageView<>(
                mappedContent,
                searchedPage.getNumber(),
                searchedPage.getSize()
        );
    }
}
