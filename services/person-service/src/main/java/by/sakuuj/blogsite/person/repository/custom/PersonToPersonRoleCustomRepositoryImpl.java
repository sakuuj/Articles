package by.sakuuj.blogsite.person.repository.custom;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonRoleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;
import by.sakuuj.blogsite.person.mapper.PersonToPersonRoleMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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
