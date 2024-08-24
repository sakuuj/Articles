package by.sakuuj.blogsite.article.entity.jpa;

import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.article.utils.UuidUtils;
import jakarta.persistence.AttributeConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class CreationId {

    private static final Map<Class<?>, String> entityClassToCreationIdPrefix = Map.of(
            ArticleEntity.class, "article:",
            TopicEntity.class, "topic:"
    );

    private final String creationId;

    private CreationId(String creationId) {
        this.creationId = creationId;
    }

    public static CreationId of(Class<?> createdEntityClass, UUID createdEntityId) {

        String uuidWithoutHyphens = UuidUtils.removeHyphens(createdEntityId);

        String creationIdPrefix = entityClassToCreationIdPrefix.get(createdEntityClass);

        if (creationIdPrefix == null) {
            throw new RuntimeException(
                    String.format("CreationId is not supported for a class '%s'", createdEntityClass.toString())
            );
        }

        String creationId = creationIdPrefix + uuidWithoutHyphens;

        return new CreationId(creationId);
    }

    @Override
    public String toString() {
        return creationId;
    }

    public static class Converter implements AttributeConverter<CreationId, String> {

        @Override
        public String convertToDatabaseColumn(CreationId attribute) {
            return attribute.getCreationId();
        }

        @Override
        public CreationId convertToEntityAttribute(String dbData) {

            return new CreationId(dbData);
        }
    }
}
