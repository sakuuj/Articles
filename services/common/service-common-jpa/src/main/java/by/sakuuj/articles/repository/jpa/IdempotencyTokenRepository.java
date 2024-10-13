package by.sakuuj.articles.repository.jpa;

import by.sakuuj.articles.entity.jpa.CreationId;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.articles.entity.jpa.entities.IdempotencyTokenEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface IdempotencyTokenRepository extends Repository<IdempotencyTokenEntity, IdempotencyTokenId> {

    Optional<IdempotencyTokenEntity> findById(IdempotencyTokenId idempotencyTokenId);

    IdempotencyTokenEntity save(IdempotencyTokenEntity entity);

    void removeByCreationId(CreationId creationId);
}
