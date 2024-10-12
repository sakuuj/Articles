package by.sakuuj.blogsite.article.controller;

import by.sakuuj.annotations.SecuredControllerTest;
import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.AuthenticatedUserTestBuilder;
import by.sakuuj.blogsite.article.PagingTestDataBuilder;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.dto.CreateRequestDTO;
import by.sakuuj.blogsite.article.dto.TopicRequest;
import by.sakuuj.blogsite.article.dto.UpdateRequestDTO;
import by.sakuuj.blogsite.article.service.ArticleService;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
import by.sakuuj.blogsite.security.AuthenticatedUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredControllerTest
@WebMvcTest(controllers = ArticleController.class)
class ArticleControllerTests extends HavingSecurityMocksPrepared {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class securedUris {

        @MethodSource
        @ParameterizedTest
        void shouldReturnStatusUnauthorizedOnTheForbiddenUris(String forbiddenUri) throws Exception {

            mockMvc.perform(get(forbiddenUri))
                    .andExpect(status().isUnauthorized());
        }

        static List<String> shouldReturnStatusUnauthorizedOnTheForbiddenUris() {
            return List.of(
                    "/articles/something/something",
                    "/random-uri"
            );
        }
    }

    @Nested
    class findById {

        @Test
        void shouldFindById_whenFoundInService() throws Exception {

            // given
            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();

            UUID idToFindBy = articleBuilder.getId();
            ArticleResponse expectedResponse = articleBuilder.buildResponse();
            String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

            when(articleService.findById(any())).thenReturn(Optional.of(expectedResponse));

            // when, then
            mockMvc.perform(get("/articles/{id}", idToFindBy))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseJson));

            verify(articleService).findById(idToFindBy);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldNotFindById_whenNotFoundInService() throws Exception {

            // given
            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();

            UUID idToFindBy = articleBuilder.getId();

            when(articleService.findById(any())).thenReturn(Optional.empty());

            // when, then
            mockMvc.perform(get("/articles/{id}", idToFindBy))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));

            verify(articleService).findById(idToFindBy);
            verifyNoMoreInteractions(articleService);
        }
    }

    @Nested
    class findAllSortedByCreatedAtDesc {

        @Test
        void shouldFindAll_whenFoundInRepo() throws Exception {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
            ArticleResponse firstResponse = articleBuilder.buildResponse();
            ArticleResponse secondResponse = articleBuilder
                    .withId(UUID.fromString("90d99038-92c0-45c1-a69a-58cfe93d8a20"))
                    .buildResponse();
            PageView<ArticleResponse> expectedResponse = pagingBuilder.aPageView(List.of(firstResponse, secondResponse));
            String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

            when(articleService.findAllSortedByCreatedAtDesc(any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseJson));

            verify(articleService).findAllSortedByCreatedAtDesc(requestedPage);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldRespondWithStatusOkAndEmptyPage_whenNotFoundInRepo() throws Exception {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            PageView<ArticleResponse> expectedResponse = pagingBuilder.emptyPageView();
            String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

            when(articleService.findAllSortedByCreatedAtDesc(any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseJson));

            verify(articleService).findAllSortedByCreatedAtDesc(requestedPage);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldRespondWithStatus4xx_whenInvalidRequestedPage() throws Exception {

            // given
            RequestedPage requestedPage = RequestedPage.aPage()
                    .withNumber(-10)
                    .withSize(-10);

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(articleService);
        }

    }

    @Nested
    class findAllBySearchTermsSortedByRelevance {

        @Test
        void shouldFindAll_whenFoundInRepo() throws Exception {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();
            String searchTerms = "red angry bird красная птица";

            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
            ArticleResponse firstResponse = articleBuilder.buildResponse();
            ArticleResponse secondResponse = articleBuilder
                    .withId(UUID.fromString("90d99038-92c0-45c1-a69a-58cfe93d8a20"))
                    .buildResponse();
            PageView<ArticleResponse> expectedResponse = pagingBuilder.aPageView(List.of(firstResponse, secondResponse));
            String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

            when(articleService.findAllBySearchTermsSortedByRelevance(any(), any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.SEARCH_TERMS_REQUEST_PARAM, searchTerms)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseJson));

            verify(articleService).findAllBySearchTermsSortedByRelevance(searchTerms, requestedPage);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldRespondWithStatusOkAndEmptyPage_whenNotFoundInRepo() throws Exception {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();
            String searchTerms = "red angry bird красная птица";

            PageView<ArticleResponse> expectedResponse = pagingBuilder.emptyPageView();
            String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

            when(articleService.findAllBySearchTermsSortedByRelevance(any(), any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.SEARCH_TERMS_REQUEST_PARAM, searchTerms)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseJson));

            verify(articleService).findAllBySearchTermsSortedByRelevance(searchTerms, requestedPage);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldRespondWithStatus4xx_whenInvalidRequestedPage() throws Exception {

            // given
            RequestedPage requestedPage = RequestedPage.aPage()
                    .withNumber(-10)
                    .withSize(-10);
            String searchTerms = "red angry bird красная птица";

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.SEARCH_TERMS_REQUEST_PARAM, searchTerms)

                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(articleService);
        }

        @Test
        void shouldRespondWithStatus4xx_whenInvalidSearchTerms() throws Exception {

            // given
            RequestedPage requestedPage = PagingTestDataBuilder.aPaging().aRequestedPage();
            String searchTerms = "";

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.SEARCH_TERMS_REQUEST_PARAM, searchTerms)
                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(articleService);
        }
    }

    @Nested
    class findAllByTopicsSortedByCreatedAtDesc {

        @Test
        void shouldFindAll_whenFoundInRepo() throws Exception {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();
            String topics = "cs,math,physics";
            List<TopicRequest> topicRequests = List.of(
                    TopicRequest.builder()
                            .name("cs")
                            .build(),
                    TopicRequest.builder()
                            .name("math")
                            .build(),
                    TopicRequest.builder()
                            .name("physics")
                            .build()
            );

            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
            ArticleResponse firstResponse = articleBuilder.buildResponse();
            ArticleResponse secondResponse = articleBuilder
                    .withId(UUID.fromString("90d99038-92c0-45c1-a69a-58cfe93d8a20"))
                    .buildResponse();
            PageView<ArticleResponse> expectedResponse = pagingBuilder.aPageView(List.of(firstResponse, secondResponse));
            String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

            when(articleService.findAllByTopicsSortedByCreatedAtDesc(any(), any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.HAVING_TOPICS_REQUEST_PARAM, topics)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseJson));

            verify(articleService).findAllByTopicsSortedByCreatedAtDesc(topicRequests, requestedPage);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldRespondWithStatusOkAndEmptyPage_whenNotFoundInRepo() throws Exception {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();
            String topics = "cs,math,physics";
            List<TopicRequest> topicRequests = List.of(
                    TopicRequest.builder()
                            .name("cs")
                            .build(),
                    TopicRequest.builder()
                            .name("math")
                            .build(),
                    TopicRequest.builder()
                            .name("physics")
                            .build()
            );

            PageView<ArticleResponse> expectedResponse = pagingBuilder.emptyPageView();
            String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

            when(articleService.findAllByTopicsSortedByCreatedAtDesc(any(), any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.HAVING_TOPICS_REQUEST_PARAM, topics)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseJson));

            verify(articleService).findAllByTopicsSortedByCreatedAtDesc(topicRequests, requestedPage);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldRespondWithErrorStatusCode_whenProvidedWithInvalidTopic() throws Exception {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();
            String topics = ",math,physics";

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.HAVING_TOPICS_REQUEST_PARAM, topics)
                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(articleService);
        }

        @Test
        void shouldRespondWithErrorStatusCode_whenProvidedWithInvalidRequestedPage() throws Exception {

            // given
            RequestedPage requestedPage = RequestedPage.aPage()
                    .withSize(-10)
                    .withNumber(-10);
            String topics = "cs,math,physics";

            // when, then
            mockMvc.perform(get("/articles")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                            .queryParam(ArticleController.HAVING_TOPICS_REQUEST_PARAM, topics)
                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(articleService);
        }
    }

    @Nested
    class create {

        @Test
        void shouldCreate() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = articleBuilder.buildRequest();
            UUID idempotencyTokenValue = UUID.fromString("ab43236d-f11b-4a54-9fbb-e271fdabb949");

            var createRequest = new CreateRequestDTO<>(
                    idempotencyTokenValue,
                    articleRequest
            );
            String createRequestJson = objectMapper.writeValueAsString(createRequest);

            UUID idOfCreated = articleBuilder.getId();

            when(articleService.create(any(), any(), any())).thenReturn(idOfCreated);

            // when, then
            mockMvc.perform(post("/articles")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createRequestJson)
                            .with(securityContext(securityContext))
                    ).andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "/articles/" + idOfCreated))
                    .andExpect(content().string(""));

            verify(articleService).create(articleRequest, idempotencyTokenValue, authenticatedUser);
            verifyNoMoreInteractions(articleService);
        }

        @Test
        void shouldNotCreate_OnInvalidCreateRequest() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            var createRequest = new CreateRequestDTO<>(
                    null,
                    null
            );
            String createRequestJson = objectMapper.writeValueAsString(createRequest);

            // when, then
            mockMvc.perform(post("/articles")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(createRequestJson)
                    .with(securityContext(securityContext))
            ).andExpect(status().is4xxClientError());

            verifyNoInteractions(articleService);
        }
    }

    @Nested
    class updateById {

        @Test
        void shouldUpdateById() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = articleBuilder.buildRequest();
            UUID idToUpdateBy = articleBuilder.getId();
            short version = articleBuilder.getVersion();

            var updateRequestDTO = new UpdateRequestDTO<>(
                    version,
                    articleRequest
            );
            String createRequestJson = objectMapper.writeValueAsString(updateRequestDTO);

            // when, then
            mockMvc.perform(put("/articles/{id}", idToUpdateBy)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createRequestJson)
                            .with(securityContext(securityContext))
                    ).andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(articleService).updateById(idToUpdateBy, articleRequest, version, authenticatedUser);
            verifyNoMoreInteractions(articleService);
        }


        @Test
        void shouldNotUpdate_onInvalidUpdateRequest() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();

            UUID idToUpdateBy = articleBuilder.getId();
            short version = articleBuilder.getVersion();

            var updateRequestDTO = new UpdateRequestDTO<>(
                    version,
                    null
            );
            String createRequestJson = objectMapper.writeValueAsString(updateRequestDTO);

            // when, then
            mockMvc.perform(put("/articles/{id}", idToUpdateBy)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(createRequestJson)
                    .with(securityContext(securityContext))
            ).andExpect(status().is4xxClientError());

            verifyNoInteractions(articleService);
        }

    }

    @Nested
    class deleteById {

        @Test
        void shouldDeleteById() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            UUID idToDeleteBy = ArticleTestDataBuilder.anArticle().getId();

            // when, then
            mockMvc.perform(delete("/articles/{id}", idToDeleteBy)
                            .with(securityContext(securityContext))
                    ).andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(articleService).deleteById(idToDeleteBy, authenticatedUser);
            verifyNoMoreInteractions(articleService);
        }
    }

    @Nested
    class addTopic {

        @Test
        void shouldAddTopic() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            UUID articleId = ArticleTestDataBuilder.anArticle().getId();
            UUID request = TopicTestDataBuilder.aTopic().getId();
            String requestJson = objectMapper.writeValueAsString(request);


            // when, then
            mockMvc.perform(patch("/articles/{articleId}/add-topic", articleId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .with(securityContext(securityContext))
            ).andExpect(status().isNoContent());

            verify(articleService).addTopic(request, articleId, authenticatedUser);
            verifyNoMoreInteractions(articleService);
        }
    }

    @Nested
    class removeTopic {

        @Test
        void shouldRemoveTopic() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            UUID articleId = ArticleTestDataBuilder.anArticle().getId();
            UUID request = TopicTestDataBuilder.aTopic().getId();
            String requestJson = objectMapper.writeValueAsString(request);

            // when, then
            mockMvc.perform(patch("/articles/{articleId}/remove-topic", articleId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .with(securityContext(securityContext))
            ).andExpect(status().isNoContent());

            verify(articleService).removeTopic(request, articleId, authenticatedUser);
            verifyNoMoreInteractions(articleService);
        }
    }
}
