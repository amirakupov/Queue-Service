package com.zoho.getzoho.service;

import com.zoho.getzoho.dto.AiInboundDto;
import com.zoho.getzoho.persistance.LeadStorage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiTaskListener {

    private LeadStorage leadStorage;

    public AiTaskListener(LeadStorage leadStorage) {
        this.leadStorage = leadStorage;
    }

    @RabbitListener(queues = "${rabbitmq.in.queue}")
    public void onAiResult(@Payload AiInboundDto aiInboundDto, @Headers Map<String, Object> headers) {
        try {
            String correlationId = aiInboundDto.getCorrelationId();
            if(correlationId == null) {
                Object h = headers.get("x-correlation-key");
            }
            String leadId = aiInboundDto.getLeadId();
            if (leadId == null || leadId.isEmpty()) {
                throw new IllegalArgumentException("Lead ID cannot be null or empty");
            }
            var exist = leadStorage.findByKey(leadId);
            if (exist == null) {
                throw new IllegalArgumentException("Lead ID " + leadId + " already exists");
            }
            leadStorage.saveOrUpdate(leadId, exist);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid lead ID");
        }
    }
}
