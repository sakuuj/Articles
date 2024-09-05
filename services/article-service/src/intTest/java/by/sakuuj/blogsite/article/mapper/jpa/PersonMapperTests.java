package by.sakuuj.blogsite.article.mapper.jpa;

import by.sakuuj.annotations.JpaMapperTest;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.PersonResponse;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@JpaMapperTest
class PersonMapperTests {

    @Autowired
    private PersonMapper personMapper;

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
