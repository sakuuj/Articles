package by.sakuuj.articles.utils;

import by.sakuuj.articles.PagingTestDataBuilder;
import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

class PagingUtilsTests {


    @Test
    void shouldTransformSliceToPageView() {
        // given
        List<String> expectedContent = List.of("abc", "bbc", "ccc");

        PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

        PageView<String> expected = pagingBuilder.aPageView(expectedContent);
        Slice<String> slice = pagingBuilder.aSlice(expectedContent);

        // when
        PageView<String> actual = PagingUtils.toPageView(slice);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldTransformRequestedPageToPageable() {
        // given
        PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

        RequestedPage requestedPage = pagingBuilder.aRequestedPage();
        Pageable expected = pagingBuilder.aPageable();

        // when
        Pageable actual = PagingUtils.toPageable(requestedPage);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldTransformRequestedPageAndSortToPageable() {
        // given

        RequestedPage requestedPage = PagingTestDataBuilder.aPaging().aRequestedPage();
        Sort sort = Sort.by("some_property");

        // when
        Pageable actual = PagingUtils.toPageable(requestedPage, sort);

        // then
        assertThat(actual.getPageSize()).isEqualTo(requestedPage.size());
        assertThat(actual.getPageNumber()).isEqualTo(requestedPage.number());
        assertThat(actual.getSort()).isEqualTo(sort);
    }

    @Test
    void shouldAddSort_whenUnsorted() {
        // given
        Sort sortToAdd = Sort.by("propertyToSortBy");
        Pageable initialPageable = PagingTestDataBuilder.aPaging().aPageable();

        // when
        assertThat(initialPageable.getSort().isUnsorted()).isTrue();
        Pageable actualPageable = PagingUtils.addSort(initialPageable, sortToAdd);

        // then
        assertThat(actualPageable.getPageSize()).isEqualTo(initialPageable.getPageSize());
        assertThat(actualPageable.getPageNumber()).isEqualTo(initialPageable.getPageNumber());

        assertThat(actualPageable.getSort()).isEqualTo(sortToAdd);
    }

    @Test
    void shouldAddSort_whenSomeSortIsPresent() {
        // given
        String nameOfPropertyAlreadySortedBy = "propertyAlreadySortedBy";
        String nameOfPropertyToSortBy = "propertyToSortBy";

        Sort sortPresentBefore = Sort.by(nameOfPropertyAlreadySortedBy);
        Sort sortToAdd = Sort.by(nameOfPropertyToSortBy);
        Pageable initialPageable = PageRequest.of(0, 10, sortPresentBefore);

        // when
        assertThat(initialPageable.getSort().isSorted()).isTrue();
        Pageable actualPageable = PagingUtils.addSort(initialPageable, sortToAdd);

        // then
        assertThat(actualPageable.getPageSize()).isEqualTo(initialPageable.getPageSize());
        assertThat(actualPageable.getPageNumber()).isEqualTo(initialPageable.getPageNumber());

        assertThat(isPageableSortedByProperty(actualPageable, nameOfPropertyAlreadySortedBy)).isTrue();
        assertThat(isPageableSortedByProperty(actualPageable, nameOfPropertyToSortBy)).isTrue();
    }

    @MethodSource
    @ParameterizedTest
    void shouldRespectDirectionOfAddedSort_andKeepDirectionOfAlreadyPresentSort(
            Direction directionOfPropertyAlreadySortedBy,
            Direction directionOfPropertyToSortBy
    ) {
        // given
        String nameOfPropertyAlreadySortedBy = "nameOfPropertyAlreadySortedBy";
        String nameOfPropertyToSortBy = "propertyToSortBy";

        Sort sortPresentBefore = Sort.by(directionOfPropertyAlreadySortedBy, nameOfPropertyAlreadySortedBy);
        Sort sortToAdd = Sort.by(directionOfPropertyToSortBy, nameOfPropertyToSortBy);
        Pageable initialPageable = PageRequest.of(0, 10, sortPresentBefore);

        // when
        assertThat(initialPageable.getSort().isSorted()).isTrue();
        Pageable actualPageable = PagingUtils.addSort(initialPageable, sortToAdd);

        // then
        assertThat(isPageableSortedByPropertyWithDirection(actualPageable, nameOfPropertyAlreadySortedBy, directionOfPropertyAlreadySortedBy))
                .isTrue();
        assertThat(isPageableSortedByPropertyWithDirection(actualPageable, nameOfPropertyToSortBy, directionOfPropertyToSortBy))
                .isTrue();
    }

    static List<Arguments> shouldRespectDirectionOfAddedSort_andKeepDirectionOfAlreadyPresentSort() {
        return List.of(
                arguments(ASC, ASC),
                arguments(ASC, DESC),
                arguments(DESC, ASC),
                arguments(DESC, DESC)
        );
    }

    @MethodSource
    @ParameterizedTest
    void shouldRespectDirectionOfAddedSort_ifWasUnsorted(Direction directionOfPropertyToSortBy) {
        // given
        String nameOfPropertyToSortBy = "propertyToSortBy";

        Sort sortToAdd = Sort.by(directionOfPropertyToSortBy, nameOfPropertyToSortBy);
        Pageable initialPageable = PageRequest.of(0, 10);

        // when
        assertThat(initialPageable.getSort().isUnsorted()).isTrue();
        Pageable actualPageable = PagingUtils.addSort(initialPageable, sortToAdd);

        // then
        assertThat(isPageableSortedByPropertyWithDirection(actualPageable, nameOfPropertyToSortBy, directionOfPropertyToSortBy))
                .isTrue();
    }

    static List<Direction> shouldRespectDirectionOfAddedSort_ifWasUnsorted() {
        return List.of(ASC, DESC);
    }

    private static boolean isPageableSortedByProperty(Pageable actualPageable, String nameOfPropertyToSortBy) {
        return actualPageable.getSort().getOrderFor(nameOfPropertyToSortBy) != null;
    }

    private static boolean isPageableSortedByPropertyWithDirection(
            Pageable actualPageable,
            String nameOfPropertyToSortBy,
            Direction sortDirection
    ) {
        Sort.Order sortOrder = actualPageable.getSort().getOrderFor(nameOfPropertyToSortBy);

        return sortOrder != null && sortOrder.getDirection().equals(sortDirection);
    }
}