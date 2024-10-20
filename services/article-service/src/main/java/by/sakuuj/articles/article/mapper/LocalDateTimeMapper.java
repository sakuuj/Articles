package by.sakuuj.articles.article.mapper;

import java.time.LocalDateTime;

public interface LocalDateTimeMapper {

    String toStringDateTime(LocalDateTime localDateTime);

    LocalDateTime toJavaDateTime(String localDateTime);
}
