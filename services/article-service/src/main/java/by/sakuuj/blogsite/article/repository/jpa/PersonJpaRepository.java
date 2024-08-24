package by.sakuuj.blogsite.article.repository.jpa;

import by.sakuuj.blogsite.article.entity.jpa.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, UUID> {
}
