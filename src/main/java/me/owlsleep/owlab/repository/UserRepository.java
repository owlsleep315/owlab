package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    long countByRoleIgnoreCase(String role);
    List<User> findTop10ByOrderByLastLoginAtDesc();

    @Query("SELECT COALESCE(SUM(u.loginCount), 0) FROM User u")
    Long sumLoginCount();
}
