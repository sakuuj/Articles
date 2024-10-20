package by.sakuuj.articles.person.mappers;

import by.sakuuj.articles.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
import by.sakuuj.articles.entity.jpa.entities.PersonRoleEntity;
import by.sakuuj.articles.entity.jpa.entities.PersonToPersonRoleEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class PersonToPersonRoleMapperImpl implements PersonToPersonRoleMapper {

    private final EntityManager entityManager;

    public PersonToPersonRoleEntity toEntity(PersonToPersonRoleId id) {

        UUID personId = id.getPersonId();
        Short personRoleId = id.getPersonRoleId();

        PersonEntity personRef = entityManager.getReference(PersonEntity.class, personId);
        PersonRoleEntity personRoleRef = entityManager.getReference(PersonRoleEntity.class, personRoleId);

        return PersonToPersonRoleEntity.builder()
                .id(id)
                .person(personRef)
                .personRole(personRoleRef)
                .build();
    }
}
