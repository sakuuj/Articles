package by.sakuuj.blogsite.person.repository;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonRoleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;
import by.sakuuj.blogsite.person.repository.custom.PersonToPersonRoleCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonToPersonRoleRepository extends JpaRepository<PersonToPersonRoleEntity, PersonToPersonRoleId>,
        PersonToPersonRoleCustomRepository {

    void removeById(PersonToPersonRoleId id);

//    void deleteAllByIdInBatch(Iterable<PersonToPersonRoleId> ids);

    List<PersonToPersonRoleEntity> findByPersonAndPersonRoleIn(PersonEntity person, List<PersonRoleEntity> personRoles);
}
