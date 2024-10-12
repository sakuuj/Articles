package by.sakuuj.blogsite.article.controller;

import by.sakuuj.annotations.SecuredControllerTest;
import by.sakuuj.blogsite.article.AuthenticatedUserTestBuilder;
import by.sakuuj.blogsite.article.PagingTestDataBuilder;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.article.dto.CreateRequestDTO;
import by.sakuuj.blogsite.article.dto.TopicRequest;
import by.sakuuj.blogsite.article.dto.TopicResponse;
import by.sakuuj.blogsite.article.dto.UpdateRequestDTO;
import by.sakuuj.blogsite.article.service.TopicService;
import by.sakuuj.blogsite.security.AuthenticatedUser;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SecuredControllerTest
@WebMvcTest(controllers = TopicController.class)
class TopicControllerTests extends HavingSecurityMocksPrepared {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;

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
                    "/topics/something/something",
                    "/random-uri"
            );
        }
    }

    @Nested
    class findById {

        @Test
        void shouldFindById_whenFoundInService() throws Exception {

            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            UUID id = topicBuilder.getId();
            TopicResponse expectedResponse = topicBuilder.buildResponse();
            String expectedJsonResponse = objectMapper.writeValueAsString(expectedResponse);

            when(topicService.findById(any())).thenReturn(Optional.of(expectedResponse));

            // when, then
            mockMvc.perform(get("/topics/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));

            verify(topicService).findById(id);
            verifyNoMoreInteractions(topicService);
        }

        @Test
        void shouldFindById_whenNotFoundInService() throws Exception {

            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            UUID id = topicBuilder.getId();

            when(topicService.findById(any())).thenReturn(Optional.empty());

            // when, then
            mockMvc.perform(get("/topics/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(""));

            verify(topicService).findById(id);
            verifyNoMoreInteractions(topicService);
        }
    }

    @Nested
    class findAllSortedByCreatedAtDesc {

        @Test
        void shouldFind_whenFoundInService() throws Exception {

            // given
            RequestedPage requestedPage = RequestedPage.aPage()
                    .withSize(10)
                    .withNumber(0);

            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicResponse firstResponse = topicBuilder.buildResponse();
            TopicResponse secondResponse = topicBuilder
                    .withId(UUID.fromString("1f46ba93-f4b1-4762-a3dc-e48356945d34"))
                    .buildResponse();

            PageView<TopicResponse> expectedResponse = PagingTestDataBuilder.aPaging()
                    .aPageView(List.of(firstResponse, secondResponse));

            String expectedJsonResponse = objectMapper.writeValueAsString(expectedResponse);

            when(topicService.findAllSortByCreatedAtDesc(any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/topics")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));

            verify(topicService).findAllSortByCreatedAtDesc(requestedPage);
            verifyNoMoreInteractions(topicService);
        }

        @Test
        void shouldFindEmptyBodyAndStatusOk_whenNotFoundInService() throws Exception {

            // given
            RequestedPage requestedPage = RequestedPage.aPage()
                    .withSize(10)
                    .withNumber(0);

            PageView<TopicResponse> expectedResponse = PagingTestDataBuilder.aPaging()
                    .emptyPageView();

            String expectedJsonResponse = objectMapper.writeValueAsString(expectedResponse);

            when(topicService.findAllSortByCreatedAtDesc(any())).thenReturn(expectedResponse);

            // when, then
            mockMvc.perform(get("/topics")
                            .queryParam("page-size", String.valueOf(requestedPage.size()))
                            .queryParam("page-number", String.valueOf(requestedPage.number()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));

            verify(topicService).findAllSortByCreatedAtDesc(requestedPage);
            verifyNoMoreInteractions(topicService);
        }

        @Test
        void  shouldRespondWith4xx_onInvalidPageRequest() throws Exception {

            // given
            RequestedPage invalidRequestedPage = RequestedPage.aPage()
                    .withSize(-10)
                    .withNumber(-10);

            // when, then
            mockMvc.perform(get("/topics")
                            .queryParam("page-size", String.valueOf(invalidRequestedPage.size()))
                            .queryParam("page-number", String.valueOf(invalidRequestedPage.number()))
                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(topicService);
        }
    }

    @Nested
    class create {

        @Test
        void shouldCreate() throws Exception {

            // given
            UUID idempotencyTokenValue = UUID.fromString("1f46ba93-f4b1-4762-a3dc-e48356945d34");
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicRequest topicRequest = topicBuilder.buildRequest();

            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();

            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            var createRequestDTO = new CreateRequestDTO<>(
                    idempotencyTokenValue,
                    topicRequest
            );

            UUID idOfCreated = topicBuilder.getId();

            when(topicService.create(any(), any(), any())).thenReturn(idOfCreated);

            String requestBodyJson = objectMapper.writeValueAsString(createRequestDTO);

            // when, then
            mockMvc.perform(
                            post("/topics")
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestBodyJson)
                                    .with(securityContext(securityContext))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "/topics/" + idOfCreated))
                    .andExpect(content().string(""));

            verify(topicService).create(topicRequest, idempotencyTokenValue, authenticatedUser);
            verifyNoMoreInteractions(topicService);
        }

        @Test
        void shouldResponseWith4xx_onInvalidCreateRequest() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            var invalidCreateRequest = new CreateRequestDTO<>(
                    null,
                    null
            );

            String requestBodyJson = objectMapper.writeValueAsString(invalidCreateRequest);

            // when, then
            mockMvc.perform(
                            post("/topics")
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestBodyJson)
                                    .with(securityContext(securityContext))
                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(topicService);
        }
    }

    @Nested
    class updateById {

        @Test
        void shouldUpdateById() throws Exception {

            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicRequest topicRequest = topicBuilder.buildRequest();
            UUID idOfTopicToUpdate = topicBuilder.getId();
            short version = topicBuilder.getVersion();

            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();

            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            var updateRequestDTO = new UpdateRequestDTO<>(
                    version,
                    topicRequest
            );

            String requestBodyJson = objectMapper.writeValueAsString(updateRequestDTO);

            // when, then
            mockMvc.perform(
                            put("/topics/{id}", idOfTopicToUpdate)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestBodyJson)
                                    .with(securityContext(securityContext))
                    )
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(topicService).updateById(idOfTopicToUpdate, topicRequest, version, authenticatedUser);
            verifyNoMoreInteractions(topicService);
        }

        @Test
        void shouldResponseWith4xx_onInvalidUpdateRequest() throws Exception {

            // given
            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();
            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            var invalidUpdateRequest = new UpdateRequestDTO<>(
                    (short) 1,
                    null
            );

            String requestBodyJson = objectMapper.writeValueAsString(invalidUpdateRequest);

            // when, then
            mockMvc.perform(
                            put("/topics")
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestBodyJson)
                                    .with(securityContext(securityContext))
                    )
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(topicService);
        }
    }

    @Nested
    class deleteById {

        @Test
        void shouldDeleteById() throws Exception {

            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            UUID idToDeleteBy = topicBuilder.getId();

            AuthenticatedUser authenticatedUser = AuthenticatedUserTestBuilder.newInstance().build();

            SecurityContext securityContext = SecurityUtils.createSecurityContext(authenticatedUser);

            // when, then
            mockMvc.perform(
                            delete("/topics/{id}", idToDeleteBy)
                                    .with(securityContext(securityContext))
                    )
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(topicService).deleteById(idToDeleteBy, authenticatedUser);
            verifyNoMoreInteractions(topicService);
        }
    }
}