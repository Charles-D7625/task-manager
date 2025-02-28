package com.example.task_manager.util;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            
            String authHeader = request.getHeader(HEADER_NAME);
    
            if(authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
    
                String token = authHeader.substring(BEARER_PREFIX.length());
                String email = jwtUtil.extractEmail(token);
    
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    
    
                    String role = jwtUtil.extractRoleFromToken(token);

                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authority, null, Collections.singleton(authority));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }
    
            }
        } catch (ExpiredJwtException exception) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\", \"message\": \"" + exception.getMessage() + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    public UUID getUserIdFromJwt(String authHeader) {

        return jwtUtil.extractId(authHeader.substring(BEARER_PREFIX.length()));
    }
}
