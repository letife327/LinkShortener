package az.texnoera.link_shortener.repository;

import az.texnoera.link_shortener.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
