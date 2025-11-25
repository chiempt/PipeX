package com.example.pipex.crm.dto.request;

import lombok.Data;

@Data
public class LeadSearchRequest {
    private String name;
    private String primaryPhone;
    private String primaryEmail;
    private Long sourceId;
    private Long stageId;
    private Long ownerUserId;
    private Long contactId;
    private String priority;
    private Long accountId;
    private Long pipelineId;
}
