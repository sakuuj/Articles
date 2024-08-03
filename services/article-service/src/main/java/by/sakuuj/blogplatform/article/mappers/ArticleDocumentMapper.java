package by.sakuuj.blogplatform.article.mappers;

import by.sakuuj.blogplatform.article.dtos.ArticleDocumentResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArticleDocumentMapper {
    ArticleDocumentResponse toResponse(ArticleDocument document);
}
