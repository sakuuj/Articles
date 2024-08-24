package by.sakuuj.blogsite.article.mappers;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {
                TopicMapper.class,
                PersonMapper.class,
                JpaReferenceMapper.class
        }
)
public interface ArticleMapper {

    @Mappings({
            @Mapping(target = "author", source = "authorId"),

            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "articleTopics", ignore = true),
            @Mapping(target = "modificationAudit", ignore = true),
    })
    ArticleEntity toEntity(ArticleRequest request, UUID authorId);

    @Mappings({
            @Mapping(target = "topics", source = "entity.articleTopics"),
            @Mapping(target = "createdAt", source = "entity.modificationAudit.createdAt"),
            @Mapping(target = "updatedAt", source = "entity.modificationAudit.updatedAt"),
    })
    ArticleResponse toResponse(ArticleEntity entity);


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "articleTopics", ignore = true),
            @Mapping(target = "modificationAudit", ignore = true),
    })
    void updateEntity(@MappingTarget ArticleEntity entity, ArticleRequest request);
}
