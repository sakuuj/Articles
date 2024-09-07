package by.sakuuj.blogsite.person.services;

import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonRoleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.PersonToPersonRoleEntity;
import by.sakuuj.blogsite.person.grpc.Email;
import by.sakuuj.blogsite.person.grpc.MaybePersonResponse;
import by.sakuuj.blogsite.person.grpc.PersonResponse;
import by.sakuuj.blogsite.person.grpc.RolesRequest;
import by.sakuuj.blogsite.person.grpc.SavePersonRequest;
import by.sakuuj.blogsite.person.grpc.UUID;
import by.sakuuj.blogsite.person.mappers.PersonMapper;
import by.sakuuj.blogsite.person.repository.PersonRepository;
import by.sakuuj.blogsite.person.repository.PersonRoleRepository;
import by.sakuuj.blogsite.person.repository.PersonToPersonRoleRepository;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public MaybePersonResponse getPersonById(UUID id) {

        java.util.UUID personId = java.util.UUID.fromString(id.getValue());

        return personRepository.findById(personId)
                .map(personMapper::toMaybePersonResponse)
                .orElse(MaybePersonResponse.getDefaultInstance());
    }

    @Override
    public PersonResponse savePerson(SavePersonRequest request) {
        PersonEntity personToSave = personMapper.toPersonEntityWithoutRoles(request);

        try {
            personRepository.save(personToSave);

            List<String> personRolesToAdd = request.getRolesList().stream()
                    .map(Enum::toString)
                    .toList();

            addRolesToPerson(personRolesToAdd, personToSave);

        } catch (DataIntegrityViolationException ex) {

            throw Status.fromCode(Status.Code.INVALID_ARGUMENT)
                    .withCause(ex)
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        return personMapper.toPersonResponse(personToSave);
    }

    @Override
    public PersonResponse blockPerson(UUID id) {

        java.util.UUID personId = java.util.UUID.fromString(id.getValue());

        PersonEntity personToBlock = personRepository.findById(personId)
                .orElseThrow(Status.NOT_FOUND::asRuntimeException);

        personToBlock.setBlocked(true);

        return personMapper.toPersonResponse(personToBlock);
    }

    @Override
    public PersonResponse unblockPerson(UUID id) {

        java.util.UUID personId = java.util.UUID.fromString(id.getValue());

        PersonEntity personToUnblock = personRepository.findById(personId)
                .orElseThrow(Status.NOT_FOUND::asRuntimeException);

        personToUnblock.setBlocked(false);

        return personMapper.toPersonResponse(personToUnblock);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PersonResponse addRolesToPerson(RolesRequest request) {

        List<String> personRoleNames = request.getRolesList().stream()
                .map(Enum::toString)
                .toList();

        java.util.UUID personId = java.util.UUID.fromString(request.getPersonId().getValue());
        PersonEntity person = personRepository.findById(personId)
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Person is not found")
                        .asRuntimeException());

        try {
            addRolesToPerson(personRoleNames, person);
        } catch (DataIntegrityViolationException ex) {

            throw Status.INVALID_ARGUMENT
                    .withCause(ex)
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        return personMapper.toPersonResponse(person);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PersonResponse removeRolesFromPerson(RolesRequest request) {

        List<String> personRoleNamesToRemove = request.getRolesList().stream()
                .map(Enum::toString)
                .toList();

        java.util.UUID personId = java.util.UUID.fromString(request.getPersonId().getValue());
        PersonEntity person = personRepository.findById(personId)
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Person is not found")
                        .asRuntimeException());

        try {
            removeRolesFromPerson(personRoleNamesToRemove, person);
        } catch (DataIntegrityViolationException ex) {

            throw Status.INVALID_ARGUMENT
                    .withCause(ex)
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        return personMapper.toPersonResponse(person);
    }

    private void addRolesToPerson(List<String> personRoleNames, PersonEntity person) {

        List<PersonRoleEntity> foundRoles = mapRoleNamesToPersonRoleEntities(personRoleNames);

        foundRoles.forEach(foundRole -> {

            var personToPersonRoleId = PersonToPersonRoleId.builder()
                    .personId(person.getId())
                    .personRoleId(foundRole.getId())
                    .build();

            personToPersonRoleRepository.save(personToPersonRoleId);
        });

        List<PersonToPersonRoleEntity> personToPersonRoleEntitiesToAdd = personToPersonRoleRepository
                .findByPersonAndPersonRoleIn(person, foundRoles);

        if (person.getPersonToPersonRoleList() == null) {
            person.setPersonToPersonRoleList(new ArrayList<>());
        }
        person.getPersonToPersonRoleList().addAll(personToPersonRoleEntitiesToAdd);
    }

    private void removeRolesFromPerson(List<String> personRoleNamesToRemove, PersonEntity person) {

        List<PersonRoleEntity> foundRolesToRemove = mapRoleNamesToPersonRoleEntities(personRoleNamesToRemove);

        foundRolesToRemove.forEach(foundRole -> {

            var personToPersonRoleId = PersonToPersonRoleId.builder()
                    .personId(person.getId())
                    .personRoleId(foundRole.getId())
                    .build();

            personToPersonRoleRepository.removeById(personToPersonRoleId);
        });

        List<PersonToPersonRoleEntity> personToPersonRoleEntitiesToRemove = personToPersonRoleRepository
                .findByPersonAndPersonRoleIn(person, foundRolesToRemove);

        if (person.getPersonToPersonRoleList() == null) {
            return;
        }
        person.getPersonToPersonRoleList().removeAll(personToPersonRoleEntitiesToRemove);
    }

    private List<PersonRoleEntity> mapRoleNamesToPersonRoleEntities(List<String> personRoleNames) {

        List<PersonRoleEntity> foundRoles = personRoleRepository.findByNameIn(personRoleNames);

        if (foundRoles.size() != personRoleNames.size()) {

            List<String> foundRoleNames = foundRoles.stream()
                    .map(PersonRoleEntity::getName)
                    .toList();

            List<String> nonExistingRoleNames = personRoleNames
                    .stream()
                    .filter(roleName -> !foundRoleNames.contains(roleName))
                    .toList();

            if (nonExistingRoleNames.isEmpty()) {
                throw Status.INVALID_ARGUMENT
                        .withDescription("One or more roles are specified multiple times")
                        .asRuntimeException();
            } else {
                throw Status.INVALID_ARGUMENT
                        .withDescription(String.format("Non existing roles are specified: %s", nonExistingRoleNames))
                        .asRuntimeException();
            }
        }
        return foundRoles;
    }

}
