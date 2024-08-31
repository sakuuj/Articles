package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends Repository<TopicEntity, UUID> {

    Optional<TopicEntity> findById(UUID id);

    Slice<TopicEntity> findAll(Pageable pageable);

    void removeById(UUID id);

    TopicEntity save(TopicEntity topicEntity);
}
