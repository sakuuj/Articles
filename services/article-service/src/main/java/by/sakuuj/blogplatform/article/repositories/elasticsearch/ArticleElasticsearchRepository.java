package by.sakuuj.blogplatform.article.repositories.elasticsearch;

import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleElasticsearchRepository extends ElasticsearchRepository<ArticleDocument, UUID>,
        ArticleCustomElasticsearchRepository {
}
