package by.sakuuj.articles.service;

import by.sakuuj.articles.entity.jpa.CreationId;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import by.sakuuj.articles.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.articles.repository.jpa.IdempotencyTokenRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdempotencyTokenServiceImplTests {

    @Mock
    private IdempotencyTokenRepository idempotencyTokenRepository;

    @InjectMocks
    private IdempotencyTokenServiceImpl idempotencyTokenServiceImpl;

    @Nested
    class findById_IdempotencyTokenId {

        @Test
        void shouldFindById_WhenEntityIsPresent() {

            // given
            UUID clientId = UUID.fromString("309affca-2e66-493e-b050-caeebff5a9c9");
            UUID idempotencyTokenValue = UUID.fromString("ed03b605-cc2d-4b9e-89cc-324ce8a38cab");
            IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            Class<ArticleEntity> createdEntityClass = ArticleEntity.class;
            UUID createdEntityId = UUID.fromString("9f3b2d35-41d3-4b4d-870c-ffe6d9a83508");
            CreationId creationId = CreationId.of(createdEntityClass, createdEntityId);

            IdempotencyTokenEntity idempotencyTokenEntity = IdempotencyTokenEntity.builder()
                    .id(idempotencyTokenId)
                    .creationId(creationId)
                    .build();

            when(idempotencyTokenRepository.findById(any())).thenReturn(Optional.of(idempotencyTokenEntity));

            // when
            Optional<IdempotencyTokenEntity> actual = idempotencyTokenServiceImpl.findById(idempotencyTokenId);

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get()).usingRecursiveComparison()
                    .isEqualTo(idempotencyTokenEntity);

            verify(idempotencyTokenRepository).findById(idempotencyTokenId);
            verifyNoMoreInteractions(idempotencyTokenRepository);
        }

        @Test
        void shouldNotFindById_WhenEntityIsNotPresent() {

            // given
            UUID clientId = UUID.fromString("309affca-2e66-493e-b050-caeebff5a9c9");
            UUID idempotencyTokenValue = UUID.fromString("ed03b605-cc2d-4b9e-89cc-324ce8a38cab");
            IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            when(idempotencyTokenRepository.findById(any())).thenReturn(Optional.empty());

            // when
            Optional<IdempotencyTokenEntity> actual = idempotencyTokenServiceImpl.findById(idempotencyTokenId);

            // then
            assertThat(actual).isEmpty();

            verify(idempotencyTokenRepository).findById(idempotencyTokenId);
            verifyNoMoreInteractions(idempotencyTokenRepository);
        }
    }

    @Nested
    class create_IdempotencyTokenId_CreationId {

        @Captor
        private ArgumentCaptor<IdempotencyTokenEntity> tokenEntityArgumentCaptor;

        @Test
        void shouldCreate() {
            UUID clientId = UUID.fromString("309affca-2e66-493e-b050-caeebff5a9c9");
            UUID idempotencyTokenValue = UUID.fromString("ed03b605-cc2d-4b9e-89cc-324ce8a38cab");
            IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            Class<ArticleEntity> createdEntityClass = ArticleEntity.class;
            UUID createdEntityId = UUID.fromString("9f3b2d35-41d3-4b4d-870c-ffe6d9a83508");
            CreationId creationId = CreationId.of(createdEntityClass, createdEntityId);

            IdempotencyTokenEntity idempotencyTokenEntity = IdempotencyTokenEntity.builder()
                    .id(idempotencyTokenId)
                    .creationId(creationId)
                    .build();

            when(idempotencyTokenRepository.save(any())).thenReturn(idempotencyTokenEntity);

            // when
            idempotencyTokenServiceImpl.create(idempotencyTokenId, creationId);

            // then
            verify(idempotencyTokenRepository).save(tokenEntityArgumentCaptor.capture());
            IdempotencyTokenEntity savedTokenEntity = tokenEntityArgumentCaptor.getValue();

            assertThat(savedTokenEntity.getId()).isEqualTo(idempotencyTokenId);
            assertThat(savedTokenEntity.getCreationId()).isEqualTo(creationId);

            verifyNoMoreInteractions(idempotencyTokenRepository);
        }
    }


    @Nested
    class removeByCreationId_CreationId {

        @Test
        void shouldRemoveByCreationId() {
            Class<ArticleEntity> createdEntityClass = ArticleEntity.class;
            UUID createdEntityId = UUID.fromString("9f3b2d35-41d3-4b4d-870c-ffe6d9a83508");
            CreationId creationId = CreationId.of(createdEntityClass, createdEntityId);

            doNothing().when(idempotencyTokenRepository).removeByCreationId(any());

            // when
            idempotencyTokenServiceImpl.deleteByCreationId(creationId);

            // then
            verify(idempotencyTokenRepository).removeByCreationId(creationId);
            verifyNoMoreInteractions(idempotencyTokenRepository);
        }
    }

}
