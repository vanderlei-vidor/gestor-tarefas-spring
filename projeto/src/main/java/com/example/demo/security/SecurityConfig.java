package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilitamos para facilitar os testes no Postman
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Qualquer requisição exige login
                )
                .httpBasic(Customizer.withDefaults()) // Permite login pelo Postman (Auth Basic)
                .formLogin(Customizer.withDefaults()); // Permite login pelo navegador (Formulário)

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Aqui definimos seu usuário e senha fixos
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12345")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}