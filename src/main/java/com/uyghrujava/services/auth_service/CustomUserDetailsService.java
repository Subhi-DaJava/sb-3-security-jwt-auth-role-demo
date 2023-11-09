package com.uyghrujava.services.auth_service;

import com.uyghrujava.dto.RegisterRequest;
import com.uyghrujava.exceptions.UserNotFoundException;
import com.uyghrujava.models.Role;
import com.uyghrujava.models.User;
import com.uyghrujava.models.UserRole;
import com.uyghrujava.repositories.UserRepository;
import com.uyghrujava.repositories.UserRoleRepository;
import com.uyghrujava.security.principal.SecurityPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;

    public CustomUserDetailsService(UserRepository userRepository,
                                    UserRoleRepository userRoleRepository,
                                    RoleService roleService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            List<UserRole> userRoles = userRoleRepository.findAllByUserId(user.get().getId());

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

            userRoles.forEach(userRole -> authorities
                    .add(new SimpleGrantedAuthority(userRole.getRole().getName())));

            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    authorities);
        }
        return null;
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String registerUser(RegisterRequest registerRequest) {
        try {
            User user = (User) dtoMapperRequestDtoToUser(registerRequest);

            user = userRepository.save(user);

            if(!registerRequest.getRoleList().isEmpty()) {

                for (String role : registerRequest.getRoleList()) {
                    Role existingRole =
                            roleService.retrieveRoleByName("ROLE_" + role.toUpperCase()); // ROLE_ADMIN, ROLE_USER etc.

                    if(existingRole != null) {
                        addUserRole(user, existingRole);
                    }
                }
            } else {
                Role defaultRole = Role.builder().name("ROLE_USER").build();
                addUserRole(user, defaultRole);
            }
            log.info("User created successfully");
            return "User created successfully";
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            //return e.getCause().getMessage();
            return "User not created, inappropriate data !!";
        }
    }

    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(RegisterRequest registerRequest) {

        User userByUsername = findUserByUsername(registerRequest.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if(userByUsername == null) {
            throw new UserNotFoundException("User not found");
        }

        //User user = (User) dtoMapperRequestDtoToUser(registerRequest);
//        userByUsername.setUserNumber(registerRequest.getUserNumber());
//        userByUsername.setUsername(registerRequest.getUsername());
//        userByUsername.setPassword(registerRequest.getPassword());
//        userByUsername.setEmail(registerRequest.getEmail());

        if(!registerRequest.getRoleList().isEmpty()) {
            for (String role : registerRequest.getRoleList()) {
                Role existingRole =
                        roleService.retrieveRoleByName("ROLE_" + role.toUpperCase()); // ROLE_ADMIN, ROLE_USER, ROLE_MANAGER etc.

                if(existingRole != null) {
                    addUserRole(userByUsername, existingRole);
                }
            }
        } else {
            addUserRole(userByUsername, null);
        }

        User userUpdated = userRepository.save(userByUsername);
        log.info("User updated successfully");
        return userUpdated;
    }

    public User findCurrentUser() {
        Optional<User> user = userRepository.findById(SecurityPrincipal.getInstance().getLoggedInPrincipal().getId());
        return user.orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public List<UserRole> findAllCurrentUserRoles() {
        return userRoleRepository.
                findAllByUserId(SecurityPrincipal.getInstance().getLoggedInPrincipal().getId());
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void addUserRole(User user, Role role) {
        if(role == null) {
            role = roleService.retrieveDefaultRole();
        }

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);
    }

//    private User dtoMapperRequestDtoToUser(RegisterRequest registerRequest) {
//        return  User.builder()
//                .username(registerRequest.getUsername())
//                .email(registerRequest.getEmail())
//                .userNumber(registerRequest.getUserNumber())
//                .password(registerRequest.getPassword())
//                .build();
//    }

    private Object dtoMapperRequestDtoToUser(RegisterRequest registerRequest) {
        User target = new User();
        target.setUserNumber(registerRequest.getUserNumber());
        target.setUsername(registerRequest.getUsername());
        target.setPassword(registerRequest.getPassword());
        target.setEmail(registerRequest.getEmail());

        return target;
    }

}
