package by.sakuuj.blogsite.article.utils;

import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionUtils {

    public static <T> T executeReadOnly(TransactionTemplate txTemplate,
                                          TransactionCallback<T> action) {
        try {
            txTemplate.setReadOnly(true);
            return txTemplate.execute(action);
        } finally {
            txTemplate.setReadOnly(false);
        }
    }
}
