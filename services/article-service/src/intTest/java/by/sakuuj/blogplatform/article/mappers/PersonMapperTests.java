package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.annotations.MapperTest;
import by.sakuuj.blogplatform.article.PersonTestDataBuilder;
import by.sakuuj.blogplatform.article.dtos.PersonRequest;
import by.sakuuj.blogplatform.article.dtos.PersonResponse;
import by.sakuuj.blogplatform.article.entities.jpa.PersonEntity;
import by.sakuuj.blogplatform.article.entities.jpa.embeddable.ModificationAudit;
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
        PersonTestDataBuilder personTestDataBuilder = PersonTestDataBuilder
                .aPerson()
                .withId(null)
                .withModificationAudit(new ModificationAudit());

        PersonRequest personRequest = personTestDataBuilder.buildRequest();
        PersonEntity expectedEntity = personTestDataBuilder.build();

        // when
        PersonEntity actualEntity = personMapper.toEntity(personRequest);

        // then
        assertThat(actualEntity).usingRecursiveComparison()
                .isEqualTo(expectedEntity);
    }

    @Test
    void shouldMapToResponse() {

        // given
        PersonTestDataBuilder personTestDataBuilder = PersonTestDataBuilder
                .aPerson();

        PersonEntity entity = personTestDataBuilder.build();
        PersonResponse expectedResponse = personTestDataBuilder.buildResponse();

        // when
        PersonResponse actual = personMapper.toResponse(entity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

    }
}
