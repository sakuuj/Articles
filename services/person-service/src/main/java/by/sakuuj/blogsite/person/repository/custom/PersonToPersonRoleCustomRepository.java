package by.sakuuj.blogsite.person.repository.custom;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;

public interface PersonToPersonRoleCustomRepository {

    PersonToPersonRoleEntity save(PersonToPersonRoleId id);
}
