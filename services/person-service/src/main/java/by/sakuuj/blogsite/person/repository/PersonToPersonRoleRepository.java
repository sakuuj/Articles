package by.sakuuj.blogsite.person.repository;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;
import by.sakuuj.blogsite.person.repository.custom.PersonToPersonRoleCustomRepository;
import org.springframework.data.repository.Repository;

public interface PersonToPersonRoleRepository extends Repository<PersonToPersonRoleEntity, PersonToPersonRoleId>,
        PersonToPersonRoleCustomRepository {

    void removeById(PersonToPersonRoleId id);
}
