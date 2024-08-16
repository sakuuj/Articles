package by.sakuuj.blogsite.article.mappers;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.entities.jpa.ArticleEntity;
import by.sakuuj.blogsite.article.entities.jpa.TopicEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                PersonMapper.class,
                TopicMapper.class,
                JpaReferenceMapper.class
        },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ArticleMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "modificationAudit", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "author", source = "authorId")
    })
    ArticleEntity toEntity(ArticleRequest request, UUID authorId);

    @Mappings({
            @Mapping(target = "topics", source = "topics"),
            @Mapping(target = "createdAt", source = "entity.modificationAudit.createdAt"),
            @Mapping(target = "updatedAt", source = "entity.modificationAudit.updatedAt")
    })
    ArticleResponse toResponse(ArticleEntity entity, List<TopicEntity> topics);
}
