package com.uyghrujava.security.principal;

import com.uyghrujava.models.User;
import com.uyghrujava.services.auth_service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class SecurityPrincipal {

    private static SecurityPrincipal securityPrincipal = null;

    private final Authentication principal = SecurityContextHolder.getContext().getAuthentication();

    private static CustomUserDetailsService userService;

    @Autowired
    private SecurityPrincipal(CustomUserDetailsService userService) {
        SecurityPrincipal.userService = userService;
    }

    public static SecurityPrincipal getInstance() {

        if(securityPrincipal == null) {
            securityPrincipal = new SecurityPrincipal(userService);
        }
        return securityPrincipal;
    }

    public User getLoggedInPrincipal() {
        if(principal != null) {
            UserDetails userDetails = (UserDetails) principal.getPrincipal();
            Optional<User> user = userService.findUserByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                return user.get();
            }
        }
        return null;
    }

    public Collection<?> getLoggedPrincipalAuthorities() {
        return ((UserDetails)principal.getPrincipal()).getAuthorities();
    }
}
