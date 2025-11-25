package com.example.pipex.crm.controller;

import com.example.pipex.common.controller.BaseController;
import com.example.pipex.crm.entity.Lead;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import com.example.pipex.common.dto.ApiResponse;
import com.example.pipex.common.utils.ApiResponseBuilder;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/crm/leads")
@Tag(name = "CRM Leads", description = "APIs for managing CRM leads")
@Slf4j
public class LeadController extends BaseController<Lead, Long> {

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Lead>>> search(@RequestParam String query) {
        List<Lead> leads = service.findAll();
        return ApiResponseBuilder.success(leads, "Search leads successfully");
    }
}
