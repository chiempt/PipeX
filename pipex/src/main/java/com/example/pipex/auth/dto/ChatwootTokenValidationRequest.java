package com.example.pipex.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatwootTokenValidationRequest {
    private String accessToken;
    private String client;
    private String expiry;
    private String tokenType;
    private String uid;
}
