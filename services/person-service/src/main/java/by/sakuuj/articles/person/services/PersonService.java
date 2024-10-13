package by.sakuuj.articles.person.services;

import by.sakuuj.articles.person.grpc.Email;
import by.sakuuj.articles.person.grpc.MaybePersonResponse;
import by.sakuuj.articles.person.grpc.PersonResponse;
import by.sakuuj.articles.person.grpc.RolesRequest;
import by.sakuuj.articles.person.grpc.SavePersonRequest;
import by.sakuuj.articles.person.grpc.UUID;

public interface PersonService {

    MaybePersonResponse getPersonByEmail(Email email);

    MaybePersonResponse getPersonById(UUID id);

    PersonResponse savePerson(SavePersonRequest request);

    PersonResponse blockPerson(UUID id);

    PersonResponse unblockPerson(UUID id);

    PersonResponse addRolesToPerson(RolesRequest request);

    PersonResponse removeRolesFromPerson(RolesRequest request);
}
