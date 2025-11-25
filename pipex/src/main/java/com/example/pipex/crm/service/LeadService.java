package com.example.pipex.crm.service;

import com.example.pipex.common.service.BaseService;
import com.example.pipex.crm.entity.Lead;
import java.util.List;

import com.example.pipex.crm.dto.request.LeadSearchRequest;
import com.example.pipex.crm.dto.response.LeadSearchResponse;

public interface LeadService extends BaseService<Lead, Long> {

    List<LeadSearchResponse> searchLeads(LeadSearchRequest request);
}
