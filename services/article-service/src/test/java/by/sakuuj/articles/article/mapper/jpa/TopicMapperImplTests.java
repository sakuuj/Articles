package by.sakuuj.articles.article.mapper.jpa;

import by.sakuuj.articles.article.TopicTestDataBuilder;
import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.TopicResponse;
import by.sakuuj.articles.entity.jpa.embeddable.ModificationAudit;
import by.sakuuj.articles.entity.jpa.entities.ArticleTopicEntity;
import by.sakuuj.articles.entity.jpa.entities.TopicEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TopicMapperImplTests {

    private final TopicMapperImpl topicMapper = new TopicMapperImpl();

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
