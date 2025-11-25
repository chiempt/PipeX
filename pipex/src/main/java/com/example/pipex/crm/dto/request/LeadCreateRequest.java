package com.example.pipex.crm.dto.request;

import java.util.Map;

import lombok.Data;

@Data
public class LeadCreateRequest {

    private String name;
    private String primaryPhone;
    private String primaryEmail;

    private Long sourceId;
    private Long stageId;
    private Long ownerId;
    private Long contactId;
    private Long userId;
    private String status;
    private String priority;
    private Integer score;

    private Map<String, Object> additionalAttributes;
}
