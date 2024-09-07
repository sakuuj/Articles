package by.sakuuj.blogsite.service;

import by.sakuuj.blogsite.person.grpc.Email;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;

public interface PersonService {

    MaybePersonResponse getPersonByEmail(Email email);

    PersonResponse savePerson(SavePersonRequest request);
}
