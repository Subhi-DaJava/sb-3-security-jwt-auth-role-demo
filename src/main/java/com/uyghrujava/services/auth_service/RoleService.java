package com.uyghrujava.services.auth_service;

import com.uyghrujava.models.Role;
import com.uyghrujava.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> retrieveAllRoles() {
        return roleRepository.findAll();
    }

    public Role retrieveDefaultRole() {
        return retrieveAllRoles().stream().findFirst().orElse(null);
    }

    /**
     * Retrieve role by name
     * @param roleName Role name
     * @return Role object
     */
    public Role retrieveRoleByName(String roleName) {
        return retrieveAllRoles().stream().filter(role -> role.getName().equals(roleName)).findFirst().orElse(null);
    }
}
