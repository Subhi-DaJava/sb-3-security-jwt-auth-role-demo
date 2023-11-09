package com.uyghrujava.repositories;

import com.uyghrujava.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findAllByUserId(Long userId);

}
