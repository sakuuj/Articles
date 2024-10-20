package by.sakuuj.articles.paging;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record RequestedPage(
        @PositiveOrZero
        int number,

        @Positive
        int size
) {

    public static RequestedPage aPage() {
        return new RequestedPage(0, 1);
    }
}