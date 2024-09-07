package by.sakuuj.blogsite.person.mappers;

import by.sakuuj.blogsite.person.grpc.Email;
import com.google.protobuf.BoolValue;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface WrapperMapper {

    default String toStringEmail(Email email) {
        return email.getValue();
    }

    default UUID toJavaUUID(by.sakuuj.blogsite.person.grpc.UUID uuid) {
        return UUID.fromString(uuid.getValue());
    }

    default BoolValue toBoolValue(boolean bool) {
        return BoolValue.of(bool);
    }

    default boolean toBoolean(BoolValue boolValue) {
        return boolValue.getValue();
    }

    default Email toGrpcEmail(String email) {
        return Email.newBuilder()
                .setValue(email)
                .build();
    }

    default by.sakuuj.blogsite.person.grpc.UUID toGrpcUUID(UUID uuid) {

        return by.sakuuj.blogsite.person.grpc.UUID.newBuilder()
                .setValue(uuid.toString())
                .build();
    }
}