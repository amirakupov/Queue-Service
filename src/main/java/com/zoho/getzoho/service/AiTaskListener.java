package com.zoho.getzoho.service;

import com.zoho.getzoho.dto.AiInboundDto;
import com.zoho.getzoho.dto.IncomingDto;
import com.zoho.getzoho.persistance.LeadStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class AiTaskListener {

    private static final Logger log = LoggerFactory.getLogger(AiTaskListener.class);

    private final LeadStorage leadStorage;

    public AiTaskListener(LeadStorage leadStorage) {
        this.leadStorage = leadStorage;
    }

    @RabbitListener(queues = "${rabbitmq.in.queue}")
    public void onAiResult(@Payload AiInboundDto dto,
                           @Header(name = "amqp_correlationId", required = false) String headerCorrelationId) {

        String correlationId = dto.getCorrelationId();
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = headerCorrelationId;
        }
        log.info("Received AI result correlationId={} leadId={} status={}",
                correlationId, dto.getLeadId(), dto.getStatus());

        String leadId = dto.getLeadId();
        if (leadId == null || leadId.isBlank()) {
            throw new IllegalArgumentException("Lead ID is missing in AI result");
        }

        IncomingDto existing = leadStorage.findByKey(leadId);
        if (existing == null) {
            throw new IllegalArgumentException("Lead ID " + leadId + " not found in storage");
        }

        if (dto.getSummary() != null) {
            existing.getNotes().add(dto.getSummary());
        }

        leadStorage.saveOrUpdate(leadId, existing);
        log.info("Updated lead {} with AI result, status={}", leadId, dto.getStatus());
    }
}
