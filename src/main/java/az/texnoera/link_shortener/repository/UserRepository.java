package az.texnoera.link_shortener.repository;

import az.texnoera.link_shortener.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);
}
