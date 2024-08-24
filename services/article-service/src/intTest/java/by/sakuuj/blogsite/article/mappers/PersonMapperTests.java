package by.sakuuj.blogsite.article.mappers;

import by.sakuuj.annotations.MapperTest;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.PersonRequest;
import by.sakuuj.blogsite.article.dtos.PersonResponse;
import by.sakuuj.blogsite.article.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.ModificationAudit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@MapperTest
public class PersonMapperTests {

    @Autowired
    private PersonMapper personMapper;

    @Test
    void shouldMapToEntity() {

        // given
        var testBuilder = PersonTestDataBuilder
                .aPerson()
                .withId(null)
                .withVersion((short) 0)
                .withModificationAudit(new ModificationAudit());

        PersonRequest personRequest = testBuilder.buildRequest();
        PersonEntity expectedEntity = testBuilder.build();

        // when
        PersonEntity actualEntity = personMapper.toEntity(personRequest);

        // then
        assertThat(actualEntity).usingRecursiveComparison()
                .isEqualTo(expectedEntity);
    }

    @Test
    void shouldMapToResponse() {

        // given
        var testBuilder = PersonTestDataBuilder
                .aPerson();

        PersonEntity entity = testBuilder.build();
        PersonResponse expectedResponse = testBuilder.buildResponse();

        // when
        PersonResponse actual = personMapper.toResponse(entity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

    }
}
