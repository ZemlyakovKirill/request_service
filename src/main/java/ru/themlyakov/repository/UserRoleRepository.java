package ru.themlyakov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.themlyakov.entity.UserRole;
import ru.themlyakov.util.Role;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByRoleIs(Role role);
}
