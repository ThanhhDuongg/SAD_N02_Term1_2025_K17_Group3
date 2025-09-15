package com.example.dorm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**", "/users/**").hasRole("ADMIN")
                        .requestMatchers("/students/new", "/students/*/edit", "/students/*/delete").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/rooms/new", "/rooms/*/edit", "/rooms/*/delete").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/contracts/new", "/contracts/*/edit", "/contracts/*/delete").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/fees/new", "/fees/*/edit", "/fees/*/delete").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/students", "/rooms", "/contracts", "/fees").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/students/*", "/rooms/*", "/contracts/*", "/fees/*").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
