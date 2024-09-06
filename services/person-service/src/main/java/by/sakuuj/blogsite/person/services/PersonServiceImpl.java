package by.sakuuj.blogsite.person.services;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.person.grpc.AddRolesRequest;
import by.sakuuj.blogsite.person.grpc.Email;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.Role;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;
import by.sakuuj.blogsite.person.mapper.PersonMapper;
import by.sakuuj.blogsite.person.repository.PersonRepository;
import by.sakuuj.blogsite.person.repository.PersonRoleRepository;
import by.sakuuj.blogsite.person.repository.PersonToPersonRoleRepository;
import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonRoleRepository personRoleRepository;
    private final PersonToPersonRoleRepository personToPersonRoleRepository;

    private final PersonMapper personMapper;

    @Override
    @Transactional(readOnly = true)
    public MaybePersonResponse getPersonByEmail(Email email) {
        return personRepository.findByPrimaryEmail(email.getValue())
                .map(personMapper::toMaybePersonResponse)
                .orElse(MaybePersonResponse.getDefaultInstance());
    }

    @Override
    public PersonResponse savePerson(SavePersonRequest request) {
        PersonEntity personToSave = personMapper.toPersonEntityWithoutRoles(request);

        personRepository.save(personToSave);

        List<String> personRolesToAdd = request.getRolesList().stream()
                .map(Role::getName)
                .toList();

        addRolesToPerson(personRolesToAdd, personToSave.getId());

        return personMapper.toPersonResponse(personToSave);
    }

    @Override
    public Empty addRolesToPerson(AddRolesRequest request) {

        UUID personId = UUID.fromString(request.getPersonId().getValue());

        List<String> personRoleNames = request.getRolesList().stream()
                .map(Role::getName)
                .toList();

        addRolesToPerson(personRoleNames, personId);

        return Empty.getDefaultInstance();
    }

    private void addRolesToPerson(List<String> personRoleNames, UUID personId) {
        personRoleRepository.findByNameIn(personRoleNames)
                .forEach(r -> {

                    var personToPersonRoleId = PersonToPersonRoleId.builder()
                            .personId(personId)
                            .personRoleId(r.getId())
                            .build();

                    personToPersonRoleRepository.save(personToPersonRoleId);
                });
    }
}
