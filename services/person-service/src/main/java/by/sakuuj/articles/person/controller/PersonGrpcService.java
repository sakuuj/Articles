package by.sakuuj.articles.person.controller;

import by.sakuuj.articles.person.grpc.Email;
import by.sakuuj.articles.person.grpc.MaybePersonResponse;
import by.sakuuj.articles.person.grpc.PersonResponse;
import by.sakuuj.articles.person.grpc.PersonServiceGrpc;
import by.sakuuj.articles.person.grpc.RolesRequest;
import by.sakuuj.articles.person.grpc.SavePersonRequest;
import by.sakuuj.articles.person.grpc.UUID;
import by.sakuuj.articles.person.services.PersonService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonGrpcService extends PersonServiceGrpc.PersonServiceImplBase {

    private final PersonService personService;

    public static <T> void handleServiceCall(
            StreamObserver<? super T> responseObserver,
            Supplier<? extends T> responseSupplier,
            String runtimeExceptionLogMessage
    ) {
        try {
            T response = responseSupplier.get();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (RuntimeException ex) {

            log.error(runtimeExceptionLogMessage, ex);

            StatusRuntimeException exception = Status.fromThrowable(ex).asRuntimeException();
            responseObserver.onError(exception);
        }
    }

    @Override
    public void getPersonByEmail(Email email, io.grpc.stub.StreamObserver<MaybePersonResponse> responseObserver) {

        handleServiceCall(
                responseObserver,
                () -> personService.getPersonByEmail(email),
                "PersonService/GetPersonByEmail exception"
        );
    }

    @Override
    public void getPersonById(UUID request, StreamObserver<MaybePersonResponse> responseObserver) {
        handleServiceCall(
                responseObserver,
                () -> personService.getPersonById(request),
                "PersonService/GetPersonById exception"
        );
    }

    @Override
    public void savePerson(SavePersonRequest request, StreamObserver<PersonResponse> responseObserver) {

        handleServiceCall(
                responseObserver,
                () -> personService.savePerson(request),
                "PersonService/SavePerson exception"
        );
    }


    @Override
    public void addRolesToPerson(RolesRequest request, StreamObserver<PersonResponse> responseObserver) {

        handleServiceCall(
                responseObserver,
                () -> personService.addRolesToPerson(request),
                "PersonService/AddRolesToPerson exception"
        );
    }

    @Override
    public void removeRolesFromPerson(RolesRequest request, StreamObserver<PersonResponse> responseObserver) {

        handleServiceCall(
                responseObserver,
                () -> personService.removeRolesFromPerson(request),
                "PersonService/RemoveRolesFromPerson exception"
        );
    }

    @Override
    public void blockPerson(UUID request, StreamObserver<PersonResponse> responseObserver) {

        handleServiceCall(
                responseObserver,
                () -> personService.blockPerson(request),
                "PersonService/BlockPerson exception"
        );
    }

    @Override
    public void unblockPerson(UUID request, StreamObserver<PersonResponse> responseObserver) {
        handleServiceCall(
                responseObserver,
                () -> personService.unblockPerson(request),
                "PersonService/UnblockPerson exception"
        );
    }

}
