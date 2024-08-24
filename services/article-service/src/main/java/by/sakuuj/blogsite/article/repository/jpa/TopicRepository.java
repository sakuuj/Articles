package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface TopicRepository extends Repository<TopicEntity, UUID> {
}
