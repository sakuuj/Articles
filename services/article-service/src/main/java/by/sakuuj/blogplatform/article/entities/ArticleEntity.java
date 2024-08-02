package by.sakuuj.blogplatform.article.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEntity {

    private UUID id;
    private String title;
    private String content;
    private List<String> topics;
    private LocalDateTime datePublishedOn;
    private LocalDateTime dateUpdatedOn;
}

