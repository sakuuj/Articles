package by.sakuuj.articles.article;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.TopicResponse;
import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.articles.article.dto.PersonResponse;
import by.sakuuj.articles.entity.jpa.embeddable.ModificationAudit;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import by.sakuuj.articles.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
import by.sakuuj.articles.entity.jpa.entities.TopicEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@With
@Getter
@AllArgsConstructor
@NoArgsConstructor(staticName = "anArticle")
public class ArticleTestDataBuilder {

    private UUID id = UUID.fromString("ce073d1b-fd75-4da4-9f1b-4e62edc822fc");

    private String title = "Java JIT Compilation";

    private String content = """
            Java HotSpot VM is a mixed-
            mode VM, which means that it
            starts off interpreting the byte-
            code, but it can (on a
            method-by-method
            basis) compile code
            into native machine
            instructions for faster
            execution.
            By passing the switch
            -XX:+PrintCompilation,
            you can see entries in
            the log file that show
            each method as it is
            compiled.
            """;

    private short version = 4324;

    private List<TopicTestDataBuilder> topicBuilders = List.of(
            TopicTestDataBuilder.aTopic(),
            TopicTestDataBuilder.aTopic()
                    .withId(UUID.fromString("222c56a6-b52c-4b9a-aad7-a5f7baa07a98"))
                    .withName("Java")
    );

    private List<String> topicNames = topicBuilders.stream()
            .map(TopicTestDataBuilder::getName)
            .collect(Collectors.toCollection(ArrayList::new));

    private List<TopicEntity> topics = topicBuilders.stream()
            .map(TopicTestDataBuilder::build)
            .collect(Collectors.toCollection(ArrayList::new));

    private List<ArticleTopicEntity> articleTopics = topicBuilders.stream()
            .map(TopicTestDataBuilder::build)
            .map(t -> ArticleTopicEntity.builder()
                    .topic(t)
                    .build()
            ).collect(Collectors.toCollection(ArrayList::new));

    private List<TopicRequest> topicRequests = topicBuilders.stream()
            .map(TopicTestDataBuilder::buildRequest)
            .collect(Collectors.toCollection(ArrayList::new));

    private List<TopicResponse> topicResponses = topicBuilders.stream()
            .map(TopicTestDataBuilder::buildResponse)
            .collect(Collectors.toCollection(ArrayList::new));

    private ModificationAudit modificationAudit = ModificationAudit.builder()
            .createdAt(LocalDateTime.of(
                    LocalDate.of(2012, 5, 10),
                    LocalTime.of(12, 59, 10)
            ))
            .updatedAt(LocalDateTime.of(
                    LocalDate.of(2013, 6, 11),
                    LocalTime.of(9, 39, 11)
            ))
            .build();

    private LocalDateTime datePublishedOn = modificationAudit.getCreatedAt();

    private PersonTestDataBuilder authorBuilder = PersonTestDataBuilder.aPerson();

    private PersonEntity author = authorBuilder.build();
    private PersonResponse authorResponseDto = authorBuilder.buildResponse();

    public ArticleResponse buildResponse() {

        return ArticleResponse.builder()
                .id(id)
                .title(title)
                .content(content)
                .topics(topicResponses)
                .author(authorResponseDto)
                .createdAt(modificationAudit.getCreatedAt())
                .updatedAt(modificationAudit.getUpdatedAt())
                .build();
    }

    public ArticleRequest buildRequest() {
        return new ArticleRequest(title, content);
    }

    public ArticleEntity build() {

        ArticleEntity built = ArticleEntity.builder()
                .id(id)
                .title(title)
                .author(author)
                .content(content)
                .version(version)
                .articleTopics(articleTopics)
                .modificationAudit(modificationAudit)
                .build();

        if (built.getArticleTopics() != null) {
            built.getArticleTopics().forEach(t -> t.setArticle(built));
        }

        return built;
    }

    public ArticleDocument buildDocument() {
        return new ArticleDocument(id, title, content, datePublishedOn);
    }
}
