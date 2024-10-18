package by.sakuuj.articles.article.controller;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.dto.CreateRequestDTO;
import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.UpdateRequestDTO;
import by.sakuuj.articles.article.service.ArticleService;
import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.security.AuthenticatedUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleController {

    private final ArticleService articleService;

    public static final String HAVING_TOPICS_REQUEST_PARAM = "having-topics";
    public static final String SEARCH_TERMS_REQUEST_PARAM = "search-terms";

    @GetMapping("/{id}")
    ResponseEntity<ArticleResponse> findById(
            @PathVariable("id") UUID id
    ) {
        return articleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    PageView<ArticleResponse> findAllSortedByCreatedAtDesc(
            @Valid RequestedPage requestedPage
    ) {
        return articleService.findAllSortedByCreatedAtDesc(requestedPage);
    }

    @GetMapping(params = {SEARCH_TERMS_REQUEST_PARAM, "!" + HAVING_TOPICS_REQUEST_PARAM})
    PageView<ArticleResponse> findAllBySearchTermsSortedByRelevance(
            @RequestParam(SEARCH_TERMS_REQUEST_PARAM) @NotBlank String searchTerms,
            @Valid RequestedPage requestedPage
    ) {
        return articleService.findAllBySearchTermsSortedByRelevance(searchTerms, requestedPage);
    }

    @GetMapping(params = {HAVING_TOPICS_REQUEST_PARAM, "!" + SEARCH_TERMS_REQUEST_PARAM})
    PageView<ArticleResponse> findAllByTopicsSortedByCreatedAtDesc(
            @RequestParam(HAVING_TOPICS_REQUEST_PARAM) List<@Valid TopicRequest> topics,
            @Valid RequestedPage requestedPage
    ) {
        return articleService.findAllByTopicsSortedByCreatedAtDesc(topics, requestedPage);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> create(
            @RequestBody @Valid CreateRequestDTO<ArticleRequest> createRequestDTO,
            AuthenticatedUser authenticatedUser
    ) {
        UUID id = articleService.create(
                createRequestDTO.payload(),
                createRequestDTO.idempotencyTokenValue(),
                authenticatedUser
        );

        URI createdUri = UriComponentsBuilder.fromPath("/articles/{id}")
                .build(id);

        return ResponseEntity.created(createdUri).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(
            @PathVariable("id") UUID id,
            AuthenticatedUser authenticatedUser
    ) {
        articleService.deleteById(id, authenticatedUser);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> updateById(
            @PathVariable("id") UUID id,
            @RequestBody @Valid UpdateRequestDTO<ArticleRequest> updateRequestDTO,
            AuthenticatedUser authenticatedUser
    ) {
        articleService.updateById(
                id,
                updateRequestDTO.payload(),
                updateRequestDTO.version(),
                authenticatedUser);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{articleId}/add-topic", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> addTopic(
            @RequestBody UUID topicId,
            @PathVariable("articleId") UUID articleId,
            AuthenticatedUser authenticatedUser
    ) {
        articleService.addTopic(topicId, articleId, authenticatedUser);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{articleId}/remove-topic", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> removeTopic(
            @RequestBody UUID topicId,
            @PathVariable("articleId") UUID articleId,
            AuthenticatedUser authenticatedUser
    ) {
        articleService.removeTopic(topicId, articleId, authenticatedUser);

        return ResponseEntity.noContent().build();
    }
}
