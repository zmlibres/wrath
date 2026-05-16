package com.seven.deadly.sin.wrath.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven.deadly.sin.wrath.common.dto.ErrorResponse;
import com.seven.deadly.sin.wrath.security.service.CustomUserDetails;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.LocalDateTime;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtServiceImpl;

    private final UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtService jwtServiceImpl,
                         UserDetailsService userDetailsService,
                         ObjectMapper objectMapper) {
        this.jwtServiceImpl = jwtServiceImpl;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException, java.io.IOException {

        String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtServiceImpl.extractUsername(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                if (jwtServiceImpl.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {

            ErrorResponse error = ErrorResponse.builder()
                                               .timestamp(LocalDateTime.now())
                                               .status(HttpStatus.UNAUTHORIZED.value())
                                               .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                                               .message("Invalid or expired token.")
                                               .build();

            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            response.getWriter().write(objectMapper.writeValueAsString(error));
        }


    }
}

