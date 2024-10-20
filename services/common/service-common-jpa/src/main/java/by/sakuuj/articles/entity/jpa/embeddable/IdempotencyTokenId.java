package by.sakuuj.articles.entity.jpa.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyTokenId {

    @Column(name = SqlAttributes.IDEMPOTENCY_TOKEN)
    private UUID idempotencyTokenValue;

    @Column(name = SqlAttributes.CLIENT_ID)
    private UUID clientId;


    public static class SqlAttributes {

        public static final String CLIENT_ID = "client_id";
        public static final String IDEMPOTENCY_TOKEN = "idempotency_token";
    }
}
