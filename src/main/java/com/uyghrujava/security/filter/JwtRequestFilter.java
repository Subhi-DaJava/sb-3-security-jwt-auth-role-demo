package com.uyghrujava.security.filter;

import com.uyghrujava.security.jwt_service.JwtTokenUtil;
import com.uyghrujava.services.auth_service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    final private JwtTokenUtil jwtTokenUtil;

    final private CustomUserDetailsService userDetailsService;

    public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {

        final String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(requestTokenHeader == null || requestTokenHeader.isEmpty() || !requestTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = requestTokenHeader.substring(7);
        //final String jwtToken = requestTokenHeader.split(" ")[1].trim();
        final String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(jwtTokenUtil.validateToken(jwtToken, userDetails)) {

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }

}
