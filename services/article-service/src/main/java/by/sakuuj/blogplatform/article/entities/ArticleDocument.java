package by.sakuuj.blogplatform.article.entities;

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

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "content", type = FieldType.Text)
    private char[] content;

    @Field(name = "date_published", type = FieldType.Date, format = DateFormat.strict_date_hour_minute_second)
    private LocalDateTime datePublishedOn;
}
