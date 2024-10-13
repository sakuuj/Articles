package by.sakuuj.articles.utils;

import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.paging.PageView;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PagingUtils {

    public static Pageable addDescSort(Pageable pageable, String propertyToSortBy){
        Sort sortByProperty = Sort.by(propertyToSortBy).descending();
        return addSort(pageable, sortByProperty);
    }

    public static Pageable addAscSort(Pageable pageable, String propertyToSortBy){
        Sort sortByProperty = Sort.by(propertyToSortBy).ascending();
        return addSort(pageable, sortByProperty);
    }

    public static Pageable addSort(Pageable pageable, Sort sortToAdd) {
        Sort initialSort = pageable.getSort();

        Sort newSort = initialSort.and(sortToAdd);

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                newSort
        );
    }

    public static Pageable toPageable(RequestedPage requestedPage) {
        return PageRequest.of(requestedPage.number(), requestedPage.size());
    }

    public static Pageable toPageable(RequestedPage requestedPage, Sort sort) {
        return PageRequest.of(requestedPage.number(), requestedPage.size(), sort);
    }

    public static <T> PageView<T> toPageView(Slice<T> page) {

        return PageView.ofContent(page.getContent())
                .withSize(page.getSize())
                .withNumber(page.getNumber());
    }
}
