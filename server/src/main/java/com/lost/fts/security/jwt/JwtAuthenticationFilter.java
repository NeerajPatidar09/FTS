package com.lost.fts.security.jwt;

import com.lost.fts.security.session.application.SessionService;
import com.lost.fts.security.userdetails.CustomUserDetails;
import com.lost.fts.security.userdetails.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
                extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final SessionService sessionService;
        private final CustomUserDetailsService userDetailsService;

        public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService,
                        SessionService sessionService) {
                this.jwtService = jwtService;
                this.userDetailsService = userDetailsService;
                this.sessionService = sessionService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                        FilterChain filterChain) throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String token = authHeader.substring(7);

                try {
                        String username = jwtService.extractUsername(token);
                        if (username == null) {
                                throw new BadCredentialsException("Invalid token");
                        }
                        if (SecurityContextHolder.getContext().getAuthentication() != null) {
                                filterChain.doFilter(request, response);
                                return;
                        }
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (!jwtService.isTokenValid(token, userDetails)) {
                                throw new BadCredentialsException("Invalid token");
                        }
                        String sessionReference = jwtService.extractSessionReference(token);

                        if (sessionReference == null ||
                                        sessionReference.isBlank()) {

                                throw new BadCredentialsException(
                                                "Missing session reference");
                        }
                        if (!sessionService.isSessionActive(sessionReference)) {

                                throw new BadCredentialsException(
                                                "Session is invalid or expired");
                        }
                        if (userDetails instanceof CustomUserDetails customUserDetails) {
                                customUserDetails.setSessionReference(sessionReference);
                        }

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        authentication.setDetails(
                                        new WebAuthenticationDetailsSource()
                                                        .buildDetails(request));

                        SecurityContextHolder.getContext()
                                        .setAuthentication(authentication);

                        // if (username != null &&
                        // SecurityContextHolder.getContext().getAuthentication() == null) {
                        // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        // if (jwtService.isTokenValid(token, userDetails)) {
                        // Claims claims = jwtService.extractAllClaims(token);
                        // String sessionId = claims.get("sid", String.class);

                        // if (sessionId == null ||
                        // !sessionService.isSessionActive(sessionId)) {

                        // throw new BadCredentialsException(
                        // "Session is invalid or expired");
                        // }
                        // UsernamePasswordAuthenticationToken authentication = new
                        // UsernamePasswordAuthenticationToken(
                        // userDetails, null, userDetails.getAuthorities());
                        // authentication.setDetails(
                        // new WebAuthenticationDetailsSource().buildDetails(request));
                        // SecurityContextHolder.getContext().setAuthentication(authentication);
                        // }
                        // }

                } catch (JwtException | IllegalArgumentException | AuthenticationException ex) {

                        SecurityContextHolder.clearContext();

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");

                        response.getWriter().write(
                                        "{\"message\":\"Invalid or expired authentication\"}");

                        return;
                }

                filterChain.doFilter(request, response);
        }
        // catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {

        // SecurityContextHolder.clearContext();

        // response.setStatus(
        // HttpServletResponse.SC_UNAUTHORIZED);

        // response.setContentType("application/json");

        // response.getWriter().write(
        // "{\"message\":\"Invalid or expired token\"}");

        // return;

        // } catch
        // (org.springframework.security.core.userdetails.UsernameNotFoundException ex)
        // {

        // SecurityContextHolder.clearContext();

        // response.setStatus(
        // HttpServletResponse.SC_UNAUTHORIZED);

        // response.setContentType("application/json");

        // response.getWriter().write(
        // "{\"message\":\"Invalid or expired token\"}");

        // return;
        // }

        // filterChain.doFilter(request, response);
        // }
}