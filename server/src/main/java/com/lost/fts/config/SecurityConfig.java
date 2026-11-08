package com.lost.fts.security.config;

import com.lost.fts.security.jwt.JwtAuthenticationFilter;
import com.lost.fts.security.userdetails.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtFilter;
        private final CustomUserDetailsService userDetailsService;
        private final PasswordEncoder passwordEncoder;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtFilter,
                        CustomUserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                this.jwtFilter = jwtFilter;
                this.userDetailsService = userDetailsService;
                this.passwordEncoder = passwordEncoder;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authenticationProvider(authenticationProvider())

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                "/api/v1/auth/login")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.OPTIONS, "/**")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(
                                                                (request, response, exception) -> {
                                                                        response.setStatus(
                                                                                        HttpStatus.UNAUTHORIZED
                                                                                                        .value());
                                                                        response.setContentType(
                                                                                        "application/json");
                                                                        response.getWriter().write(
                                                                                        "{\"message\":\"Authentication required\"}");
                                                                }))

                                .addFilterBefore(
                                                jwtFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration)
                        throws Exception {

                return configuration.getAuthenticationManager();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of("http://localhost:5173"));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET", "POST", "PUT",
                                                "PATCH", "DELETE", "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of("Authorization", "Content-Type"));

                configuration.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**", configuration);

                return source;
        }
}