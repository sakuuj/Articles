package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.dtos.TopicRequest;
import by.sakuuj.blogplatform.article.dtos.TopicResponse;
import by.sakuuj.blogplatform.article.entities.jpa.TopicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TopicMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "modificationAudit", ignore = true),
    })
    TopicEntity toEntity(TopicRequest request);

    @Mappings({
            @Mapping(target = "createdAt", source = "entity.modificationAudit.createdAt"),
            @Mapping(target = "updatedAt", source = "entity.modificationAudit.updatedAt")
    })
    TopicResponse toResponse(TopicEntity entity);
}
