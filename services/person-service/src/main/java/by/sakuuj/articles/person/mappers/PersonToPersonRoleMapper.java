package by.sakuuj.articles.person.mappers;

import by.sakuuj.articles.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.articles.entity.jpa.entities.PersonToPersonRoleEntity;

public interface PersonToPersonRoleMapper {

    PersonToPersonRoleEntity toEntity(PersonToPersonRoleId id);
}
