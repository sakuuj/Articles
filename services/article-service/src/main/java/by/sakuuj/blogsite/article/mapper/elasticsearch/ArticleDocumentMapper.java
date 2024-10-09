package by.sakuuj.blogsite.article.mapper.elasticsearch;

import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ArticleDocumentMapper {

    @Mapping(target = "datePublishedOn", source = "entity.modificationAudit.createdAt")
    ArticleDocument toDocument(ArticleEntity entity);

    @Mapping(target = "datePublishedOn", source = "response.createdAt")
    ArticleDocument toDocument(ArticleResponse response);
}
