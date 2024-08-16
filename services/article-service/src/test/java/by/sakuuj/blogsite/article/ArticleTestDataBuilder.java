package by.sakuuj.blogsite.article;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.dtos.PersonResponse;
import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.article.entities.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.entities.jpa.ArticleEntity;
import by.sakuuj.blogsite.article.entities.jpa.PersonEntity;
import by.sakuuj.blogsite.article.entities.jpa.TopicEntity;
import by.sakuuj.blogsite.article.entities.jpa.embeddable.ModificationAudit;
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

    private List<TopicTestDataBuilder> topicBuilders = List.of(
            TopicTestDataBuilder.aTopic(),
            TopicTestDataBuilder.aTopic()
                    .withId(UUID.fromString("222c56a6-b52c-4b9a-aad7-a5f7baa07a98"))
                    .withName("Java")
    );

    private List<TopicEntity> topics = topicBuilders.stream()
            .map(TopicTestDataBuilder::build)
            .collect(Collectors.toCollection(ArrayList::new));

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
                .createdAt(modificationAudit.getCreatedAt())
                .updatedAt(modificationAudit.getUpdatedAt())
                .author(authorResponseDto)
                .build();
    }

    public ArticleRequest buildRequest() {
        return new ArticleRequest(title, content, topicRequests);
    }

    public ArticleEntity build() {

        return ArticleEntity.builder()
                .id(id)
                .title(title)
                .content(content)
                .modificationAudit(modificationAudit)
                .author(author)
                .build();
    }

    public ArticleDocument buildDocument() {
        return new ArticleDocument(id, title, content, datePublishedOn);
    }
}
