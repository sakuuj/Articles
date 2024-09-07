package by.sakuuj.blogsite.service;

import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.blogsite.repository.jpa.IdempotencyTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class IdempotencyTokenServiceImpl implements IdempotencyTokenService {

    private final IdempotencyTokenRepository idempotencyTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<IdempotencyTokenEntity> findById(IdempotencyTokenId id) {

        return idempotencyTokenRepository.findById(id);
    }

    @Override
    public void create(IdempotencyTokenId idempotencyTokenId, CreationId creationId) {
        IdempotencyTokenEntity idempotencyTokenToSave = IdempotencyTokenEntity.builder()
                .id(idempotencyTokenId)
                .creationId(creationId)
                .build();

        idempotencyTokenRepository.save(idempotencyTokenToSave);
    }

    @Override
    public void deleteByCreationId(CreationId creationId) {

        idempotencyTokenRepository.removeByCreationId(creationId);
    }
}

