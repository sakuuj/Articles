package by.sakuuj.articles;

import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@With
@Getter
@AllArgsConstructor
@NoArgsConstructor(staticName = "aPaging")
public class PagingTestDataBuilder {

    private int pageSize = 66;

    private int pageNumber = 44;


    public RequestedPage aRequestedPage() {
        return RequestedPage.aPage()
                .withSize(pageSize)
                .withNumber(pageNumber);
    }

    public Pageable aPageable() {
        return PageRequest.of(pageNumber, pageSize);
    }

    public <T> Slice<T> aSlice(List<T> content) {
        return new SliceImpl<>(content, aPageable(), false);
    }

    public <T> Slice<T> emptySlice() {
        return aSlice(List.of());
    }

    public <T> PageView<T> aPageView(List<T> content) {
        return PageView.ofContent(content)
                .withNumber(pageNumber)
                .withSize(pageSize);
    }

    public <T> PageView<T> emptyPageView() {
        return aPageView(List.of());
    }

}
