package by.sakuuj.articles.person.repository;

import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
import by.sakuuj.articles.entity.jpa.utils.EntityGraphNames;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {

    @EntityGraph(value = EntityGraphNames.PERSON_EAGER_WITH_PERSON_ROLES_EAGER)
    Optional<PersonEntity> findById(UUID id);

    @EntityGraph(value = EntityGraphNames.PERSON_EAGER_WITH_PERSON_ROLES_EAGER)
    Optional<PersonEntity> findByPrimaryEmail(String primaryEmail);
}
