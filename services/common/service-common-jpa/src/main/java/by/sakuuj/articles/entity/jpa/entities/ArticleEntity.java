package by.sakuuj.articles.entity.jpa.entities;

import by.sakuuj.articles.entity.jpa.embeddable.ModificationAudit;
import by.sakuuj.articles.entity.jpa.utils.EntityGraphNames;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "articles")
@NamedEntityGraph(name = EntityGraphNames.ARTICLE_EAGER_WITH_ARTICLE_TOPICS_EAGER,
        attributeNodes = {
                @NamedAttributeNode(ArticleEntity_.AUTHOR),
                @NamedAttributeNode(value = ArticleEntity_.ARTICLE_TOPICS, subgraph = "articleTopics_eager")
        },
        subgraphs = {
                @NamedSubgraph(name = "articleTopics_eager", attributeNodes = {
                        @NamedAttributeNode(ArticleTopicEntity_.TOPIC),
                        @NamedAttributeNode(ArticleTopicEntity_.ARTICLE)
                })
        }
)
public class ArticleEntity {

    @Id
    @Column(name = SqlAttributes.ID)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = SqlAttributes.TITLE)
    private String title;

    @Column(name = SqlAttributes.CONTENT)
    private String content;

    @Embedded
    @Builder.Default
    private ModificationAudit modificationAudit = new ModificationAudit();

    @JoinColumn(name = SqlAttributes.AUTHOR_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PersonEntity author;

    @Builder.Default
    @OneToMany(mappedBy = ArticleTopicEntity_.ARTICLE)
    private List<ArticleTopicEntity> articleTopics = new ArrayList<>();

    @Version
    private short version;

    public static class SqlAttributes {
        public static final String ID = "article_id";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String AUTHOR_ID = "author_id";
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

        if (!(o instanceof ArticleEntity a)) {
            return false;
        }

        return getId() != null
                && getId().equals(a.getId());
    }
}
