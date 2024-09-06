package by.sakuuj.blogsite.person.repository.custom;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;

public interface PersonToPersonRoleCustomRepository {

    void save(PersonToPersonRoleId id);
}
