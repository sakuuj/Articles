package by.sakuuj.blogsite.article.mapper.jpa;

import by.sakuuj.annotations.JpaMapperTest;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.ModificationAudit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@JpaMapperTest
class TopicMapperTests {

    @Autowired
    private TopicMapper topicMapper;

    @Test
    void shouldMapToEntity() {

        // given
        var testBuilder = TopicTestDataBuilder
                .aTopic()
                .withId(null)
                .withVersion((short) 0)
                .withModificationAudit(new ModificationAudit());

        TopicRequest topicRequest = testBuilder.buildRequest();
        TopicEntity expectedEntity = testBuilder.build();

        // when
        TopicEntity actualEntity = topicMapper.toEntity(topicRequest);

        // then
        assertThat(actualEntity).usingRecursiveComparison()
                .isEqualTo(expectedEntity);
    }

    @Test
    void shouldMapArticleTopicToEntity() {

        // given
        var testBuilder = TopicTestDataBuilder.aTopic();

        TopicEntity expectedEntity = testBuilder.build();

        ArticleTopicEntity articleTopicEntity = ArticleTopicEntity.builder()
                .topic(expectedEntity)
                .build();

        // when
        TopicEntity actualEntity = topicMapper.toEntity(articleTopicEntity);

        // then
        assertThat(actualEntity).usingRecursiveComparison()
                .isEqualTo(expectedEntity);
    }

    @Test
    void shouldMapToResponse() {

        // given
        var testBuilder = TopicTestDataBuilder.aTopic();

        TopicEntity entity = testBuilder.build();
        TopicResponse expectedResponse = testBuilder.buildResponse();

        // when
        TopicResponse actual = topicMapper.toResponse(entity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

    }

    @Test
    void shouldUpdateEntity() {

        // given
        var testBuilder = TopicTestDataBuilder.aTopic();

        TopicEntity oldTopic = testBuilder
                .withName("old name")
                .build();

        TopicTestDataBuilder testBuilderUpdated = testBuilder
                .withName("new name");

        TopicRequest updateData = testBuilderUpdated.buildRequest();
        TopicEntity expected = testBuilderUpdated.build();

        // when
        topicMapper.updateEntity(oldTopic, updateData);

        // then
        assertThat(oldTopic).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
