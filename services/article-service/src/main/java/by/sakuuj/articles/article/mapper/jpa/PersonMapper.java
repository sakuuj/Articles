package by.sakuuj.articles.article.mapper.jpa;

import by.sakuuj.articles.article.dto.PersonResponse;
import by.sakuuj.articles.article.mapper.LocalDateTimeMapper;
import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = LocalDateTimeMapper.class
)
public interface PersonMapper {

    @Mapping(target = "createdAt", source = "entity.modificationAudit.createdAt")
    @Mapping(target = "updatedAt", source = "entity.modificationAudit.updatedAt")
    PersonResponse toResponse(PersonEntity entity);
}
