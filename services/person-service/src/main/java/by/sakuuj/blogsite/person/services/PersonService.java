package by.sakuuj.blogsite.person.services;

import by.sakuuj.blogsite.person.grpc.Email;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.RolesRequest;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;
import by.sakuuj.blogsite.person.grpc.UUID;

public interface PersonService {

    MaybePersonResponse getPersonByEmail(Email email);

    MaybePersonResponse getPersonById(UUID id);

    PersonResponse savePerson(SavePersonRequest request);

    PersonResponse blockPerson(UUID id);

    PersonResponse unblockPerson(UUID id);

    PersonResponse addRolesToPerson(RolesRequest request);

    PersonResponse removeRolesFromPerson(RolesRequest request);
}
