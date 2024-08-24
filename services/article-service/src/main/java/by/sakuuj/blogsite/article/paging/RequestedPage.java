package by.sakuuj.blogsite.article.paging;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record RequestedPage(int number, int size) {

    public static RequestedPage aPage() {
        return new RequestedPage(0, 0);
    }
}