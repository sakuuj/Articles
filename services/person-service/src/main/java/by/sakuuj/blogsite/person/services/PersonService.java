package by.sakuuj.blogsite.person.services;

import by.sakuuj.blogsite.person.grpc.AddRolesRequest;
import by.sakuuj.blogsite.person.grpc.Email;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;
import com.google.protobuf.Empty;

public interface PersonService {

    MaybePersonResponse getPersonByEmail(Email email);

    PersonResponse savePerson(SavePersonRequest request);

    Empty addRolesToPerson(AddRolesRequest request);
}
