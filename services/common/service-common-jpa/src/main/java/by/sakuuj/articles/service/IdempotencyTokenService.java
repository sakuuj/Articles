package by.sakuuj.articles.service;

import by.sakuuj.articles.entity.jpa.CreationId;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.articles.entity.jpa.entities.IdempotencyTokenEntity;

import java.util.Optional;

public interface IdempotencyTokenService {

    Optional<IdempotencyTokenEntity> findById(IdempotencyTokenId id);

    void create(IdempotencyTokenId idempotencyTokenId, CreationId creationId);

    void deleteByCreationId(CreationId creationId);

}
