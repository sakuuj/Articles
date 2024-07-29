package by.sakuuj.blogplatform.article.repositories.elasticsearch;

import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.repositories.PageView;
import org.springframework.data.domain.Pageable;

public interface ArticleCustomElasticsearchRepository {
    PageView<ArticleDocument> findMostRelevantDocuments(String searchTerms, Pageable pageable);
}
