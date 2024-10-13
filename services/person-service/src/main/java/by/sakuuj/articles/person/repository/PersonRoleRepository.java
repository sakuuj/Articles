package by.sakuuj.articles.person.repository;

import by.sakuuj.articles.entity.jpa.entities.PersonRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PersonRoleRepository extends JpaRepository<PersonRoleEntity, Short> {

    List<PersonRoleEntity> findByNameIn(Collection<String> roleNames);
}
