package by.sakuuj.blogsite.article.controller;

import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
import by.sakuuj.blogsite.article.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
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

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        System.out.println(authentication.getClass());
        Jwt principal = (Jwt) authentication.getPrincipal();
        System.out.println(principal.getClaims());

        return topicService.findAllSortByCreatedAtDesc(requestedPage);
    }


}