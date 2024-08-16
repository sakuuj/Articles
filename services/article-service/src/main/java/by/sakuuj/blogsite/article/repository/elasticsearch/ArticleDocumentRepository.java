package by.sakuuj.blogsite.article.repository.elasticsearch;

import by.sakuuj.blogsite.article.entities.elasticsearch.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleDocumentRepository extends ElasticsearchRepository<ArticleDocument, UUID>,
        ArticleDocumentComplexQueryRepository {
}
