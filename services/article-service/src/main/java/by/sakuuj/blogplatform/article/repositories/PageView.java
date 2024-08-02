package by.sakuuj.blogplatform.article.repositories;

import lombok.Builder;

import java.util.List;
import java.util.function.Function;

@Builder
public record PageView<T> (
    List<T> content,
    int requestedSize,
    int pageNumber) {

    public <R> PageView<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream()
                .map(mapper)
                .toList();

        return new PageView<>(
                mappedContent,
                requestedSize,
                pageNumber
        );
    }
}
