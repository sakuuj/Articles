package by.sakuuj.articles.paging;

import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.function.Function;

@With
@Builder
public record PageView<T>(
        List<T> content,
        int number,
        int size
) {

    public static <T> PageView<T> ofContent(List<T> content) {
        return new PageView<>(content, 0, 0);
    }

    public static <T> PageView<T> empty() {
        return new PageView<>(List.of(), 0, 0);
    }

    public PageView<T> withNumberAndSize(RequestedPage requestedPage) {
        return withNumber(requestedPage.number())
                .withSize(requestedPage.size());

    }

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