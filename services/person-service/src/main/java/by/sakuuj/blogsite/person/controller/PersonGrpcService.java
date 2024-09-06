package by.sakuuj.blogsite.person.controller;

import by.sakuuj.blogsite.person.grpc.AddRolesRequest;
import by.sakuuj.blogsite.person.grpc.Email;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonServiceGrpc;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;
import by.sakuuj.blogsite.person.services.PersonService;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonGrpcService extends PersonServiceGrpc.PersonServiceImplBase {

    private final PersonService personService;

    @Override
    public void getPersonByEmail(Email email, io.grpc.stub.StreamObserver<MaybePersonResponse> responseObserver) {
        try {
            MaybePersonResponse maybePerson = personService.getPersonByEmail(email);

            responseObserver.onNext(maybePerson);
            responseObserver.onCompleted();

        } catch (RuntimeException ex) {

            log.error("PersonService/GetPersonByEmail exception", ex);

            StatusRuntimeException exception = Status.fromThrowable(ex).asRuntimeException();
            responseObserver.onError(exception);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void savePerson(SavePersonRequest request, StreamObserver<PersonResponse> responseObserver) {
        try {
            PersonResponse personResponse = personService.savePerson(request);

            responseObserver.onNext(personResponse);
            responseObserver.onCompleted();

        } catch (RuntimeException ex) {

            log.error("PersonService/SavePerson exception", ex);

            StatusRuntimeException exception = Status.fromThrowable(ex).asRuntimeException();
            responseObserver.onError(exception);
        }
    }

    @Override
    public void addRolesToPerson(AddRolesRequest request, StreamObserver<Empty> responseObserver) {
        try {
            Empty empty = personService.addRolesToPerson(request);

            responseObserver.onNext(empty);
            responseObserver.onCompleted();

        } catch (DataIntegrityViolationException ex) {

            StatusRuntimeException exception = Status.fromCode(Status.Code.INVALID_ARGUMENT)
                    .withDescription("One of the roles you are trying to assign has been already assigned")
                    .asRuntimeException();

            responseObserver.onError(exception);

        } catch (RuntimeException ex) {

            log.error("PersonService/AddRolesToPerson exception", ex);

            StatusRuntimeException exception = Status.fromThrowable(ex).asRuntimeException();
            responseObserver.onError(exception);
        }
    }


}
