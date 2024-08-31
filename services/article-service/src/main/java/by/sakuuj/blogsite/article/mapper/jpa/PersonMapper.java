package by.sakuuj.blogsite.article.mapper.jpa;

import by.sakuuj.blogsite.article.dtos.PersonRequest;
import by.sakuuj.blogsite.article.dtos.PersonResponse;
import by.sakuuj.blogsite.article.entity.jpa.entities.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modificationAudit", ignore = true)
    PersonEntity toEntity(PersonRequest request);

    @Mapping(target = "createdAt", source = "entity.modificationAudit.createdAt")
    @Mapping(target = "updatedAt", source = "entity.modificationAudit.updatedAt")
    PersonResponse toResponse(PersonEntity entity);
}
