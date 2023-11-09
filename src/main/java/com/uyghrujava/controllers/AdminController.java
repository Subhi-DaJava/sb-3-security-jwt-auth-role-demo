package com.uyghrujava.controllers;

import com.uyghrujava.dto.EntityResponse;
import com.uyghrujava.dto.RegisterRequest;
import com.uyghrujava.models.Role;
import com.uyghrujava.services.auth_service.CustomUserDetailsService;
import com.uyghrujava.services.auth_service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * sb3_jwt_role_051123
 */

@RestController
@RequestMapping("admin")
public class AdminController {

    private final CustomUserDetailsService userService;

    private final RoleService roleService;

    public AdminController(CustomUserDetailsService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users-list")
    public ResponseEntity<Object> getAllUsers() {
        return EntityResponse.generateResponse("Retrieve all users", HttpStatus.OK, userService.retrieveAllUsers());
    }

    @GetMapping("/roles-list")
    public ResponseEntity<Object> getAllRoles() {
        return EntityResponse.generateResponse("Retrieve all roles", HttpStatus.OK, roleService.retrieveAllRoles());
    }

    @PostMapping("/add-role")
    public ResponseEntity<Object> addRole(@RequestBody Role role) {
        return EntityResponse.generateResponse("New role successfully added to DB: ", HttpStatus.CREATED, roleService.saveRole(role));
    }

    @PutMapping("/update-user")
    public ResponseEntity<Object> updateUser(@RequestBody RegisterRequest user) {
        return EntityResponse.generateResponse("User successfully updated: ", HttpStatus.OK, userService.updateUser(user));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return EntityResponse.generateResponse("Retrieve user by id: ", HttpStatus.OK, userService.findUserById(id));
    }

}
