package com.example.pipex.crm.service;

import com.example.pipex.common.repository.GenericRepositoryFactory;
import com.example.pipex.common.service.BaseServiceImpl;
import com.example.pipex.crm.entity.Lead;
import com.example.pipex.crm.dto.request.LeadSearchRequest;
import com.example.pipex.crm.dto.response.LeadSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadServiceImpl extends BaseServiceImpl<Lead, Long> implements LeadService {

    @SuppressWarnings("unchecked")
    public LeadServiceImpl(GenericRepositoryFactory repositoryFactory) {
        super((JpaRepository<Lead, Long>) (JpaRepository<?, ?>) repositoryFactory.createRepository(Lead.class));
    }

    @Override
    public List<LeadSearchResponse> searchLeads(LeadSearchRequest request) {
        return null;
    }
}
