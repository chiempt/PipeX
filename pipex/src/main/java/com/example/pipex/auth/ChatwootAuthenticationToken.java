package com.example.pipex.auth;

import com.example.pipex.auth.dto.ChatwootTokenValidationResponse;
import com.example.pipex.auth.dto.ChatwootUserData;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom Authentication token for Chatwoot authentication
 * Stores ChatwootUserData and validation result in SecurityContext
 */
public class ChatwootAuthenticationToken extends AbstractAuthenticationToken {

    private final ChatwootUserData principal;
    private final ChatwootTokenValidationResponse validationResult;

    public ChatwootAuthenticationToken(ChatwootUserData principal, ChatwootTokenValidationResponse validationResult) {
        super(getAuthorities(principal));
        this.principal = principal;
        this.validationResult = validationResult;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(ChatwootUserData userData) {
        if (userData == null || userData.getRole() == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userData.getRole().toUpperCase()));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public ChatwootUserData getPrincipal() {
        return principal;
    }

    public ChatwootTokenValidationResponse getValidationResult() {
        return validationResult;
    }
}
