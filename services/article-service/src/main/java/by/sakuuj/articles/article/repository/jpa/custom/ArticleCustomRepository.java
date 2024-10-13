package by.sakuuj.articles.article.repository.jpa.custom;

import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import by.sakuuj.articles.paging.PageView;

import java.util.List;
import java.util.UUID;

public interface ArticleCustomRepository {

    List<ArticleEntity> findAllByIdsInOrder(List<UUID> ids);

    PageView<ArticleEntity> findAllByTopicsAndSortByCreatedAtDesc(List<String> topicNames, RequestedPage requestedPage);
}
