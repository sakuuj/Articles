package by.sakuuj.blogplatform.article.entities.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("article")
@Setting(useServerConfiguration = true)
@Document(indexName = "articles", createIndex = false)
public class ArticleDocument {

    @Id
    private UUID id;

    @Field(name = ElasticsearchFieldNames.TITLE, type = FieldType.Text)
    private String title;

    @Field(name = ElasticsearchFieldNames.CONTENT, type = FieldType.Text)
    private String content;

    @Field(
            name = ElasticsearchFieldNames.DATE_PUBLISHED,
            type = FieldType.Date,
            format = DateFormat.strict_date_hour_minute_second
    )
    private LocalDateTime datePublishedOn;

    public static class ElasticsearchFieldNames {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String DATE_PUBLISHED = "date_published";
    }
}
