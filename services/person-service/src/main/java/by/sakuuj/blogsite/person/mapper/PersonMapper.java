package by.sakuuj.blogsite.person.mapper;

import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = {PersonRoleMapper.class, WrapperMapper.class, PersonToPersonRoleMapper.class}
)
public interface PersonMapper {

    @Mapping(target = "rolesList", source = "entity.personToPersonRoleList")
    PersonResponse toPersonResponse(PersonEntity entity);

    @Mapping(target = "personResponse", source = "entity")
    MaybePersonResponse toMaybePersonResponse(PersonEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modificationAudit", ignore = true)
    @Mapping(target = "personToPersonRoleList", ignore = true)
    PersonEntity toPersonEntityWithoutRoles(SavePersonRequest request);
}
