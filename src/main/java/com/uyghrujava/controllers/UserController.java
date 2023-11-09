package com.uyghrujava.controllers;

import com.uyghrujava.dto.EntityResponse;
import com.uyghrujava.dto.RegisterRequest;
import com.uyghrujava.dto.auth_request_response.AuthRequest;
import com.uyghrujava.dto.auth_request_response.AuthResponse;
import com.uyghrujava.exceptions.ResponseStatusException;
import com.uyghrujava.security.jwt_service.JwtTokenUtil;
import com.uyghrujava.services.auth_service.CustomUserDetailsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@Slf4j
@RequestMapping("user/")
public class UserController {
    
    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService userService;

    private final JwtTokenUtil jwtTokenUtil;

    private final PasswordEncoder passwordEncoder;

    public UserController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userService,
                          JwtTokenUtil jwtTokenUtil,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth")
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            authenticateUser(authRequest.username(), authRequest.password());   // authenticate the user
        } catch (BadCredentialsException e) {
            log.error("Error: {}", e.getMessage());
           return EntityResponse.generateResponse("Authentication: ", HttpStatus.UNAUTHORIZED, "Bad Credentials");

        }
        final UserDetails userDetails = userService.loadUserByUsername(authRequest.username());   // load the user details

        final String jwtToken = jwtTokenUtil.generateToken(userDetails);   // generate the JWT token
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);   // generate the refresh token

        return EntityResponse.generateResponse("Authentication: ", HttpStatus.OK,
                new AuthResponse(jwtToken, refreshToken));
    }

    @SneakyThrows
    private void authenticateUser(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            log.error("Error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not created. Register first");

        } catch (BadCredentialsException e) {
            log.error("Error: {}", e.getMessage());
            throw new BadCredentialsException(e.getMessage());        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest registerRequest) {

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        return EntityResponse.
                generateResponse("User successfully registered: ", HttpStatus.OK, userService.registerUser(registerRequest));
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile() {
        return EntityResponse.generateResponse("Retrieve user profile: ", HttpStatus.OK, userService.findCurrentUser());
    }

    @GetMapping("/roles-list")
    public ResponseEntity<Object> getUserRoleList() {
        return EntityResponse.generateResponse("Retrieve user role list: ", HttpStatus.OK, userService.findAllCurrentUserRoles());
    }

}
