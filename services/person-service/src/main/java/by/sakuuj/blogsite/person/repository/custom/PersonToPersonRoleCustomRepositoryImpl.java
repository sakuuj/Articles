package by.sakuuj.blogsite.person.repository.custom;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;
import by.sakuuj.blogsite.person.mappers.PersonToPersonRoleMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonToPersonRoleCustomRepositoryImpl implements PersonToPersonRoleCustomRepository {

    private final EntityManager entityManager;
    private final PersonToPersonRoleMapper personToPersonRoleMapper;


    @Override
    public void save(PersonToPersonRoleId id) {

        PersonToPersonRoleEntity entityToPersist = personToPersonRoleMapper.toEntity(id);

        entityManager.persist(entityToPersist);
    }
}
