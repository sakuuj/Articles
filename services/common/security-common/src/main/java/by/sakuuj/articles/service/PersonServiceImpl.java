package by.sakuuj.articles.service;

import by.sakuuj.articles.person.grpc.Email;
import by.sakuuj.articles.person.grpc.MaybePersonResponse;
import by.sakuuj.articles.person.grpc.PersonResponse;
import by.sakuuj.articles.person.grpc.PersonServiceGrpc;
import by.sakuuj.articles.person.grpc.SavePersonRequest;
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
