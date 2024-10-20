package by.sakuuj.articles.article.controller;

import by.sakuuj.articles.article.dto.CreateRequestDTO;
import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.TopicResponse;
import by.sakuuj.articles.article.dto.UpdateRequestDTO;
import by.sakuuj.articles.article.service.TopicService;
import by.sakuuj.articles.controller.resolvers.RequestedPageArgumentResolver;
import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE)
public class TopicController {

    private static final String SECURITY_REQ_NAME = "Bearer Authentication";
    private final TopicService topicService;

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> findById(
            @PathVariable("id") UUID id
    ) {
        return topicService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(parameters = {
            @Parameter(
                    name = RequestedPageArgumentResolver.PAGE_SIZE_PARAM,
                    required = true,
                    schema = @Schema(implementation = int.class)
            ),
            @Parameter(
                    name = RequestedPageArgumentResolver.PAGE_NUMBER_PARAM,
                    required = true,
                    schema = @Schema(implementation = int.class)
            )
    })
    public PageView<TopicResponse> findAllSortedByCreatedAtDesc(
            @Parameter(hidden = true) @Valid RequestedPage requestedPage
    ) {
        return topicService.findAllSortByCreatedAtDesc(requestedPage);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = SECURITY_REQ_NAME)
    public ResponseEntity<TopicResponse> create(
            @Parameter(hidden = true) AuthenticatedUser authenticatedUser,
            @RequestBody @Valid CreateRequestDTO<TopicRequest> createRequest
    ) {
        UUID id = topicService.create(
                createRequest.payload(),
                createRequest.idempotencyTokenValue(),
                authenticatedUser
        );

        URI createdUri = UriComponentsBuilder.fromPath("/topics/{id}")
                .build(id);

        return ResponseEntity.created(createdUri)
                .build();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = SECURITY_REQ_NAME)
    public ResponseEntity<Void> deleteById(
            @Parameter(hidden = true) AuthenticatedUser authenticatedUser,
            @PathVariable("id") UUID id
    ) {
        topicService.deleteById(id, authenticatedUser);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = SECURITY_REQ_NAME)
    public ResponseEntity<Void> updateById(
            @Parameter(hidden = true) AuthenticatedUser authenticatedUser,
            @RequestBody @Valid UpdateRequestDTO<TopicRequest> updateRequest,
            @PathVariable("id") UUID id
    ) {
        topicService.updateById(
                id,
                updateRequest.payload(),
                updateRequest.version(),
                authenticatedUser
        );

        return ResponseEntity.noContent().build();
    }
}
