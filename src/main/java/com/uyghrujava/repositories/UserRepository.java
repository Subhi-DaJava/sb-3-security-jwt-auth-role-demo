package com.uyghrujava.repositories;

import com.uyghrujava.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findUserByUsernameAndPassword(String username, String password);
}
