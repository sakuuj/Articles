package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.blogsite.article.entity.jpa.CreationId;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.article.entity.jpa.entities.IdempotencyTokenEntity;
import org.springframework.data.repository.Repository;

public interface IdempotencyTokenRepository extends Repository<IdempotencyTokenEntity, IdempotencyTokenId> {

    IdempotencyTokenEntity save(IdempotencyTokenEntity entity);

    void removeByCreationId(CreationId creationId);
}
