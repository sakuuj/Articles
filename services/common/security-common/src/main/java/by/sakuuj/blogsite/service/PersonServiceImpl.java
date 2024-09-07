package by.sakuuj.blogsite.service;

import by.sakuuj.blogsite.person.grpc.Email;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonServiceGrpc;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonServiceGrpc.PersonServiceBlockingStub blockingStub;

    @Override
    public MaybePersonResponse getPersonByEmail(Email email) {

        return blockingStub.getPersonByEmail(email);
    }

    @Override
    public PersonResponse savePerson(SavePersonRequest request) {

        return blockingStub.savePerson(request);
    }
}
