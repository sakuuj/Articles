package by.sakuuj.blogsite.article.mappers;

import by.sakuuj.annotations.MapperTest;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.article.entities.jpa.TopicEntity;
import by.sakuuj.blogsite.article.entities.jpa.embeddable.ModificationAudit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@MapperTest
class TopicMapperTests {

    @Autowired
    private TopicMapper topicMapper;

    @Test
    void shouldMapToEntity() {

        // given
        TopicTestDataBuilder topicTestDataBuilder = TopicTestDataBuilder
                .aTopic()
                .withId(null)
                .withModificationAudit(new ModificationAudit());

        TopicRequest topicRequest = topicTestDataBuilder.buildRequest();
        TopicEntity expectedEntity = topicTestDataBuilder.build();

        // when
        TopicEntity actualEntity = topicMapper.toEntity(topicRequest);

        // then
        assertThat(actualEntity).usingRecursiveComparison()
                .isEqualTo(expectedEntity);
    }

    @Test
    void shouldMapToResponse() {

        // given
        TopicTestDataBuilder topicTestDataBuilder = TopicTestDataBuilder
                .aTopic();

        TopicEntity entity = topicTestDataBuilder.build();
        TopicResponse expectedResponse = topicTestDataBuilder.buildResponse();

        // when
        TopicResponse actual = topicMapper.toResponse(entity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

    }
}
