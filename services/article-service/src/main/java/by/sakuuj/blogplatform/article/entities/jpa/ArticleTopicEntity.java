package by.sakuuj.blogplatform.article.entities.jpa;

import by.sakuuj.blogplatform.article.entities.jpa.embeddable.ArticleTopicId;
import by.sakuuj.blogplatform.article.entities.jpa.embeddable.ModificationAudit;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "article_topics")
public class ArticleTopicEntity implements Persistable<ArticleTopicId> {

    @EmbeddedId
    @Builder.Default
    private ArticleTopicId id = new ArticleTopicId();

    @MapsId("articleId")
    @JoinColumn(name = SqlAttributes.ARTICLE_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ArticleEntity article;

    @MapsId("topicId")
    @JoinColumn(name = SqlAttributes.TOPIC_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TopicEntity topic;

    @Embedded
    @Builder.Default
    private ModificationAudit modificationAudit = new ModificationAudit();

    @Override
    public boolean isNew() {

        return id.getArticleId() == null && id.getTopicId() == null;
    }

    public static class SqlAttributes {
        public static final String ARTICLE_ID = "article_id";
        public static final String TOPIC_ID = "topic_id";
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof ArticleTopicEntity at)) {
            return false;
        }

        return getId() != null
                && getId().equals(at.getId());
    }
}
