package by.sakuuj.blogplatform.article.utils;

import by.sakuuj.blogplatform.article.controller.RequestedPage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
}
