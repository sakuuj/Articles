package by.sakuuj.blogsite.person.mappers;

import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;
import by.sakuuj.blogsite.person.grpc.Role;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface  PersonRoleMapper {

     default Role toRole(PersonToPersonRoleEntity entity) {

        String personRoleName = entity.getPersonRole().getName();

        return Role.valueOf(personRoleName);
    }
}
