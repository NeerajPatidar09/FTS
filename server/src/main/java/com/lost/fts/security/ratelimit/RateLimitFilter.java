package com.lost.fts.security.ratelimit;

import java.io.IOException;
import java.time.Duration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/api/v1/auth/login")) {
            if (!rateLimitService.allow("login:" + request.getRemoteAddr(), 5, Duration.ofMinutes(1))) {

                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Too many requests. Try again later.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
