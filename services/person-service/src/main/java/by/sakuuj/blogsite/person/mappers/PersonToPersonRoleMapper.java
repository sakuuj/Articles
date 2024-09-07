package by.sakuuj.blogsite.person.mappers;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;

public interface PersonToPersonRoleMapper {

    PersonToPersonRoleEntity toEntity(PersonToPersonRoleId id);
}
