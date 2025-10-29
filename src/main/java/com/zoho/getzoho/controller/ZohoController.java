package com.zoho.getzoho.controller;

import com.zoho.getzoho.dto.IncomingDto;
import com.zoho.getzoho.service.IngestResult;
import com.zoho.getzoho.service.LeadService;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ZohoController {


    public ZohoController(LeadService leadService) {
        this.leadService = leadService;
    }

    private final LeadService leadService;

    @PostMapping("/leads")
    public IngestResult postLeads(@RequestBody List<IncomingDto> payload) {
        try {
            IngestResult body = leadService.saveBatch(payload);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof HttpMediaTypeException) {
                HttpMediaTypeException httpMediaTypeException = (HttpMediaTypeException) e;
            }
        }


        return null;
    }

    @GetMapping("/leads/list")
    public Collection<IncomingDto> getLeads() {
        return leadService.listLeads();
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }

}
