package com.lost.fts.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.lost.fts.security.jwt.JwtAuthenticationFilter;
import com.lost.fts.security.userdetails.CustomUserDetailsService;

// import com.lost.fts.security.jwt.JwtAuthenticationFilter;
// import com.lost.fts.security.userdetails.CustomUserDetailsService;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import java.util.List;

// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity
// public class SecurityConfig {

//         private final JwtAuthenticationFilter jwtFilter;
//         private final CustomUserDetailsService userDetailsService;
//         private final PasswordEncoder passwordEncoder;

//         @Value("${app.cors.allowed-origins}")
//         private String allowedOrigins;

//         public SecurityConfig(JwtAuthenticationFilter jwtFilter, CustomUserDetailsService userDetailsService,
//                         PasswordEncoder passwordEncoder) {

//                 this.jwtFilter = jwtFilter;
//                 this.userDetailsService = userDetailsService;
//                 this.passwordEncoder = passwordEncoder;
//         }

//         @Bean
//         public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//                 http.cors(cors -> cors.configurationSource(corsConfigurationSource(allowedOrigins)))
//                                 .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                                                 .ignoringRequestMatchers(
//                                                                 "/api/v1/auth/login",
//                                                                 "/api/v1/auth/verify-mfa",
//                                                                 "/api/v1/auth/verify-mfa-recovery",
//                                                                 "/api/v1/auth/forgot-password",
//                                                                 "/api/v1/auth/reset-password"))
//                                 .sessionManagement(session -> session
//                                                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                                 .authenticationProvider(authenticationProvider())
//                                 .authorizeHttpRequests(auth -> auth.requestMatchers(
//                                                 "/api/v1/auth/login",
//                                                 "/api/v1/auth/verify-mfa",
//                                                 "/api/v1/auth/verify-mfa-recovery",
//                                                 "/api/v1/auth/refresh",
//                                                 "/api/v1/auth/forgot-password",
//                                                 "/api/v1/auth/reset-password").permitAll()
//                                                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers(
//                                                                 "/api/v1/auth/mfa/**",
//                                                                 "/api/v1/auth/change-password")
//                                                 .authenticated().anyRequest().authenticated())
//                                 .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, exception) -> {
//                                         response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                                         response.setContentType("application/json");
//                                         response.setCharacterEncoding("UTF-8");

//                                         response.getWriter().write(
//                                                         "{\"message\":\"Authentication required\"}");
//                                 })).addFilterBefore(
//                                                 jwtFilter,
//                                                 UsernamePasswordAuthenticationFilter.class);

//                 return http.build();
//         }

//         @Bean
//         public AuthenticationProvider authenticationProvider() {
//                 DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
//                 provider.setPasswordEncoder(passwordEncoder);
//                 return provider;
//         }

//         @Bean
//         public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//                 return configuration.getAuthenticationManager();
//         }

//         @Bean
//         public CorsConfigurationSource corsConfigurationSource(
//                         @Value("${app.security.allowed-origins}") String corskey) {

//                 CorsConfiguration configuration = new CorsConfiguration();
//                 configuration.setAllowedOrigins(List.of(corskey));
//                 configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//                 configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//                 configuration.setAllowCredentials(true);
//                 configuration.setMaxAge(3600L);
//                 UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//                 source.registerCorsConfiguration("/api/**", configuration);
//                 return source;
//         }
// }

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtFilter;
        private final CustomUserDetailsService userDetailsService;
        private final PasswordEncoder passwordEncoder;

        public SecurityConfig(JwtAuthenticationFilter jwtFilter, CustomUserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                this.jwtFilter = jwtFilter;
                this.userDetailsService = userDetailsService;
                this.passwordEncoder = passwordEncoder;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider())
                                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                                "/api/v1/auth/login",
                                                "/api/v1/auth/verify-mfa",
                                                "/api/v1/auth/verify-mfa-recovery",
                                                "/api/v1/auth/refresh",
                                                "/api/v1/auth/forgot-password",
                                                "/api/v1/auth/reset-password").permitAll()
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/auth/sessions",
                                                                "/api/v1/auth/sessions/current",
                                                                "/api/v1/auth/devices")
                                                .authenticated().requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/v1/auth/sessions",
                                                                "/api/v1/auth/sessions/**")
                                                .authenticated().requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/auth/devices/*/block")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers(
                                                                "/api/v1/auth/mfa/**",
                                                                "/api/v1/auth/change-password")
                                                .authenticated().anyRequest().authenticated())
                                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, exception) -> {
                                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                        response.setContentType("application/json");
                                        response.setCharacterEncoding("UTF-8");
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
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:5173"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(false);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/**", configuration);
                return source;
        }
}