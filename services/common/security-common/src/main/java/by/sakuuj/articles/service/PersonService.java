package by.sakuuj.articles.service;

import by.sakuuj.articles.person.grpc.Email;
import by.sakuuj.articles.person.grpc.MaybePersonResponse;
import by.sakuuj.articles.person.grpc.PersonResponse;
import by.sakuuj.articles.person.grpc.SavePersonRequest;

public interface PersonService {

    MaybePersonResponse getPersonByEmail(Email email);

    PersonResponse savePerson(SavePersonRequest request);
}
