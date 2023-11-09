package com.uyghrujava.security.security_config;

import com.uyghrujava.security.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.passwordEncoder = passwordEncoder;
    }


    // Login Controller
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        final List<GlobalAuthenticationConfigurerAdapter> configurers = new ArrayList<>();

        configurers.add(new GlobalAuthenticationConfigurerAdapter() {

            @Override
            public void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
            }
        });

        return configuration.getAuthenticationManager();
    }

    // Shared Security Configuration for all Controllers
    private void sharedSecurityConfiguration(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Cross-Origin Resource Sharing disabled or enabled ?
                //.cors(AbstractHttpConfigurer::disable) // disable Cross-Origin Resource Sharing
                //.cors(cors -> cors.configurationSource(corsConfigurationSource())) // enable Cross-Origin Resource Sharing
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    // Security Configuration for Global API, path user and admin should be authenticated
    @Bean
    public SecurityFilterChain securityFilterChainGlobalAPI(HttpSecurity http) throws Exception {
        sharedSecurityConfiguration(http);
        return http.securityMatcher("user", "admin", "/user/products").authorizeHttpRequests(
                authorizeRequests -> authorizeRequests
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChainUserAPI(HttpSecurity http) throws Exception {
        sharedSecurityConfiguration(http);
        return http.securityMatcher("user/profile", "/roles_list").authorizeHttpRequests(
                authorizeRequests -> authorizeRequests
                        .anyRequest()
                        .hasRole("USER"))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChainAdminAPI(HttpSecurity http) throws Exception {
        sharedSecurityConfiguration(http);
        return http.securityMatcher("admin/**").authorizeHttpRequests(
                authorizeRequests -> authorizeRequests
                        .anyRequest()
                        .hasRole("ADMIN"))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChainLoginAPI(HttpSecurity http) throws Exception {
        sharedSecurityConfiguration(http);
        return http.securityMatcher("user/authenticate").authorizeHttpRequests(
                authorizeRequests -> authorizeRequests
                        .anyRequest()
                        .permitAll())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChainRegisterApi(HttpSecurity http) throws Exception {
        sharedSecurityConfiguration(http);
        return http.securityMatcher("user/register").authorizeHttpRequests(
                authorizeRequests -> authorizeRequests
                        .anyRequest()
                        .permitAll())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(Collections.singletonList("*"));
            cors.setAllowedMethods(Collections.singletonList("*"));
            cors.setAllowedHeaders(Collections.singletonList("*"));
            return cors;
        };
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        final var source = new UrlBasedCorsConfigurationSource();
//        final var cors = new CorsConfiguration();
//
//        cors.setAllowedOrigins(Collections.singletonList("*"));
//        cors.setAllowedMethods(Collections.singletonList("*"));
//        cors.setAllowedHeaders(Collections.singletonList("*"));
//
//        cors.addAllowedOrigin("*");
//        cors.addAllowedHeader("*");
//        cors.addAllowedMethod("*");
//
//        source.registerCorsConfiguration("/**", cors);
//
//        return source;
//    }

}
