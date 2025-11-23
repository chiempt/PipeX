package com.example.pipex.crm.controller;

import com.example.pipex.common.controller.BaseController;
import com.example.pipex.crm.entity.Lead;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/crm/leads")
@Tag(name = "CRM Leads", description = "APIs for managing CRM leads")
@Slf4j
public class LeadController extends BaseController<Lead, Long> {

}
