package com.example.pipex.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatwootUserData {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private Integer accountId;
}
