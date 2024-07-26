package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.dtos.ArticleRequest;
import by.sakuuj.blogplatform.article.dtos.ArticleResponse;
import by.sakuuj.blogplatform.article.dtos.ArticleSearchResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.entities.ArticleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "datePublishedOn", ignore = true)
    @Mapping(target = "dateUpdatedOn", ignore = true)
    ArticleEntity toEntity(ArticleRequest dto);

    ArticleResponse toResponse(ArticleEntity entity);

    ArticleSearchResponse toSearchResponse(ArticleDocument document);
}
