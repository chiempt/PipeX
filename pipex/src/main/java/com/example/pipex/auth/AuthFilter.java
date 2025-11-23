package com.example.pipex.auth;

import com.example.pipex.auth.dto.ChatwootTokenValidationResponse;
import com.example.pipex.auth.service.ChatwootAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthFilter extends OncePerRequestFilter {

    private final ChatwootAuthService chatwootAuthService;

    public AuthFilter(ChatwootAuthService chatwootAuthService) {
        this.chatwootAuthService = chatwootAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader("access-token");
        String client = request.getHeader("client");
        String expiry = request.getHeader("expiry");
        String tokenType = request.getHeader("token-type");
        String uid = request.getHeader("uid");

        if (accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> authPayload = new HashMap<>();
        authPayload.put("access-token", accessToken);
        authPayload.put("client", client);
        authPayload.put("expiry", expiry);
        authPayload.put("token-type", tokenType);
        authPayload.put("uid", uid);

        ChatwootTokenValidationResponse validationResponse = chatwootAuthService.validateAuth(authPayload);

        if (validationResponse != null && validationResponse.isValid() && validationResponse.getData() != null) {
            String role = validationResponse.getData().getRole() != null 
                    ? validationResponse.getData().getRole() 
                    : "USER";
            
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    validationResponse.getData(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication set for user: {} (account: {})", 
                    validationResponse.getData().getId(), 
                    validationResponse.getData().getAccountId());
        } else {
            log.debug("Token validation failed or no auth headers present");
        }

        filterChain.doFilter(request, response);
    }
}

