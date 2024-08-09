package by.sakuuj.blogplatform.article.repository;

import lombok.Builder;

import java.util.List;
import java.util.function.Function;

@Builder
public record PageView<T> (
    List<T> content,
    int number,
    int size
    ) {

    public <R> PageView<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream()
                .map(mapper)
                .toList();

        return new PageView<>(
                mappedContent,
                number,
                size
        );
    }
}