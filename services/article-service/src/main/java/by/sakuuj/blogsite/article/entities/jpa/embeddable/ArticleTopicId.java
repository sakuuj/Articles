package by.sakuuj.blogsite.article.entities.jpa.embeddable;

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
}
