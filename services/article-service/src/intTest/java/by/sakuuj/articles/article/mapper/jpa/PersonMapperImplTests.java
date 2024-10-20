package by.sakuuj.articles.article.mapper.jpa;

import by.sakuuj.articles.article.PersonTestDataBuilder;
import by.sakuuj.articles.article.configs.FormatConfig;
import by.sakuuj.articles.article.dto.PersonResponse;
import by.sakuuj.articles.article.mapper.LocalDateTimeMapperImpl;
import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = FormatConfig.class)
@Import({PersonMapperImpl.class, LocalDateTimeMapperImpl.class})
class PersonMapperImplTests {

    @Autowired
    private PersonMapperImpl personMapper;

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
