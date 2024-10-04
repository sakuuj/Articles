package by.sakuuj.blogsite.repository.jpa;

import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.entity.jpa.entities.IdempotencyTokenEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface IdempotencyTokenRepository extends Repository<IdempotencyTokenEntity, IdempotencyTokenId> {

    Optional<IdempotencyTokenEntity> findById(IdempotencyTokenId idempotencyTokenId);

    IdempotencyTokenEntity save(IdempotencyTokenEntity entity);

    void removeByCreationId(CreationId creationId);
}
