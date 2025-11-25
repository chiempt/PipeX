package com.example.pipex.crm.dto.response;

import lombok.Data;

@Data
public class LeadSearchResponse {
    private Long id;
    private String name;
    private String priority;
    private Long contactId;
    private ContactResponse contact;
    private SourceResponse source;
    private StageResponse stage;
    private UserResponse user;
}

