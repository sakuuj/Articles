package by.sakuuj.articles.entity.jpa.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTopicId {

    private UUID articleId;
    private UUID topicId;

    public static class SqlAttributes {

        public static final String ARTICLE_ID = "article_id";
        public static final String TOPIC_ID = "topic_id";
    }
}
