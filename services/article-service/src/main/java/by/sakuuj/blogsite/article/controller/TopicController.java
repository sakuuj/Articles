package by.sakuuj.blogsite.article.controller;

import by.sakuuj.blogsite.article.dto.CreateRequestDTO;
import by.sakuuj.blogsite.article.dto.TopicRequest;
import by.sakuuj.blogsite.article.dto.TopicResponse;
import by.sakuuj.blogsite.article.dto.UpdateRequestDTO;
import by.sakuuj.blogsite.article.service.TopicService;
import by.sakuuj.blogsite.security.AuthenticatedUser;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> findById(@PathVariable("id") UUID id) {

        return topicService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->  ResponseEntity.notFound().build());
    }

    @GetMapping
    public PageView<TopicResponse> findAllSortedByCreatedAtDesc(@Valid RequestedPage requestedPage) {

        return topicService.findAllSortByCreatedAtDesc(requestedPage);
    }

    @PostMapping
    public ResponseEntity<TopicResponse> create(AuthenticatedUser authenticatedUser,
                                                @RequestBody @Valid CreateRequestDTO<TopicRequest> createRequest) {
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
    public ResponseEntity<Void> deleteById(AuthenticatedUser authenticatedUser,
                                           @PathVariable("id") UUID id) {
        topicService.deleteById(id, authenticatedUser);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateById(AuthenticatedUser authenticatedUser,
                                           @RequestBody @Valid UpdateRequestDTO<TopicRequest> updateRequest,
                                           @PathVariable("id") UUID id) {
        topicService.updateById(
                id,
                updateRequest.payload(),
                updateRequest.version(),
                authenticatedUser
        );

        return ResponseEntity.noContent().build();
    }
}
