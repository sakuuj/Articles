package by.sakuuj.articles.article.mapper.jpa;

import by.sakuuj.articles.article.PersonTestDataBuilder;
import by.sakuuj.articles.article.dto.PersonResponse;
import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonMapperImplTests {

    private final PersonMapperImpl personMapper = new PersonMapperImpl();

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
