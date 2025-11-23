package com.example.pipex.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ChatwootTokenValidationResponse {
    private boolean isValid;
    private ChatwootUserData data;
    private Map<String, Object> error;
}
