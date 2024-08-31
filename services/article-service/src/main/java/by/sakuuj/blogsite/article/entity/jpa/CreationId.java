package by.sakuuj.blogsite.article.entity.jpa;

import by.sakuuj.blogsite.article.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.article.utils.UuidUtils;
import jakarta.persistence.AttributeConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/**
 * <pre>
 * Represents a custom id of the format:
 *      &lt;prefix>:&lt;uuid-value>
 * Prefixes depend on classes that CreationId works with and are defined
 * in the filed {@link CreationId#entityClassToCreationIdPrefix}.
 * </pre>
 */
@Getter
@EqualsAndHashCode
public class CreationId {

    private static final Map<Class<?>, String> entityClassToCreationIdPrefix = Map.of(
            ArticleEntity.class, "article:",
            TopicEntity.class, "topic:"
    );

    private final String creationIdValue;

    private CreationId(String creationIdValue) {
        this.creationIdValue = creationIdValue;
    }

    public static CreationId of(Class<?> createdEntityClass, UUID createdEntityId) {

        String uuidWithoutHyphens = UuidUtils.removeHyphens(createdEntityId);

        String creationIdPrefix = entityClassToCreationIdPrefix.get(createdEntityClass);

        if (creationIdPrefix == null) {
            throw new IllegalArgumentException(
                    String.format("CreationId is not supported for a class '%s'", createdEntityClass.toString())
            );
        }

        String creationId = creationIdPrefix + uuidWithoutHyphens;

        return new CreationId(creationId);
    }

    @Override
    public String toString() {
        return creationIdValue;
    }

    public static class Converter implements AttributeConverter<CreationId, String> {

        @Override
        public String convertToDatabaseColumn(CreationId attribute) {
            return attribute.getCreationIdValue();
        }

        @Override
        public CreationId convertToEntityAttribute(String dbData) {

            return new CreationId(dbData);
        }
    }
}
