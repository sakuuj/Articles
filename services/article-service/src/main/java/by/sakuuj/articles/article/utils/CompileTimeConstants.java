package by.sakuuj.articles.article.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CompileTimeConstants {
    public static final String EXECUTOR_BEAN_NAME = "taskExecutor";

    public static final int MAX_TOPICS_PER_PAGE_REQUEST = 50;

    public static final String TEMPORAL_ARTICLES_QUEUE_NAME = "articles";
}
