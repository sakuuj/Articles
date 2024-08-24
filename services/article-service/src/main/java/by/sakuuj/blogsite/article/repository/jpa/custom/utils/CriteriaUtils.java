package by.sakuuj.blogsite.article.repository.jpa.custom.utils;

import by.sakuuj.blogsite.article.paging.PageView;
import by.sakuuj.blogsite.article.paging.RequestedPage;
import lombok.experimental.UtilityClass;
import org.hibernate.query.Query;

import java.util.List;

@UtilityClass
public class CriteriaUtils {

    public static <T> PageView<T> getPagedQueryResult(RequestedPage requestedPage,
                                                      Query<T> createdQuery) {
        int pageSize = requestedPage.size();
        int pageNumber = requestedPage.number();

        int firstResult = pageNumber * pageSize;

        List<T> queryResult = createdQuery
                .setMaxResults(pageSize)
                .setFirstResult(firstResult)
                .list();

        return PageView.ofContent(queryResult)
                .withNumber(pageNumber)
                .withSize(pageSize);
    }
}
