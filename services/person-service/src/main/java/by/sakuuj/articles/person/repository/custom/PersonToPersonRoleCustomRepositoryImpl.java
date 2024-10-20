package by.sakuuj.articles.person.repository.custom;

import by.sakuuj.articles.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.articles.entity.jpa.entities.PersonToPersonRoleEntity;
import by.sakuuj.articles.person.mappers.PersonToPersonRoleMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonToPersonRoleCustomRepositoryImpl implements PersonToPersonRoleCustomRepository {

    private final EntityManager entityManager;
    private final PersonToPersonRoleMapper personToPersonRoleMapper;


    @Override
    public PersonToPersonRoleEntity save(PersonToPersonRoleId id) {

        PersonToPersonRoleEntity entityToPersist = personToPersonRoleMapper.toEntity(id);

        entityManager.persist(entityToPersist);

        return entityToPersist;
    }
}
