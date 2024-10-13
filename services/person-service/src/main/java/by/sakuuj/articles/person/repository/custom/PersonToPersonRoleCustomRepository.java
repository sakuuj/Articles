package by.sakuuj.articles.person.repository.custom;

import by.sakuuj.articles.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.articles.entity.jpa.entities.PersonToPersonRoleEntity;

public interface PersonToPersonRoleCustomRepository {

    PersonToPersonRoleEntity save(PersonToPersonRoleId id);
}
