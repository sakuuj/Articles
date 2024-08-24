package by.sakuuj.blogsite.article.repository.jpa.custom;

import by.sakuuj.blogsite.article.paging.RequestedPage;
import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.paging.PageView;

import java.util.List;
import java.util.UUID;

public interface ArticleCustomRepository {

    List<ArticleEntity> findAllByIdsInOrder(List<UUID> ids);

    PageView<ArticleEntity> findAllByTopicsAndSortByCreatedAtDesc(List<String> topicNames, RequestedPage requestedPage);
}
