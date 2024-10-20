package by.sakuuj.articles.person.mappers;

import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
import by.sakuuj.articles.person.grpc.MaybePersonResponse;
import by.sakuuj.articles.person.grpc.PersonResponse;
import by.sakuuj.articles.person.grpc.SavePersonRequest;
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

    @Mapping(target = "isBlocked", source = "blocked")
    @Mapping(target = "rolesList", source = "entity.personToPersonRoleList")
    PersonResponse toPersonResponse(PersonEntity entity);

    @Mapping(target = "personResponse", source = "entity")
    MaybePersonResponse toMaybePersonResponse(PersonEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isBlocked", ignore = true)
    @Mapping(target = "modificationAudit", ignore = true)
    @Mapping(target = "personToPersonRoleList", ignore = true)
    PersonEntity toPersonEntityWithoutRoles(SavePersonRequest request);
}
