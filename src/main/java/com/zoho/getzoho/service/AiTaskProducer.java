package com.zoho.getzoho.service;

import com.zoho.getzoho.dto.AiOutboundDto;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiTaskProducer {

    final AmqpTemplate amqpTemplate;
    final CorrelationIDGenerator correlationIDGenerator;

    private static final String UUID_RE = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";

    private final String exchange;

    private final String routingKey;

    public AiTaskProducer(AmqpTemplate amqpTemplate,
                          CorrelationIDGenerator correlationIDGenerator,
                          @Value("${rabbitmq.out.exchange}") String exchange,
                          @Value("${rabbitmq.out.routingkey}") String routingKey){
        this.amqpTemplate=amqpTemplate;
        this.correlationIDGenerator=correlationIDGenerator;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String sendTask(AiOutboundDto aiOutboundDto) {
        if (aiOutboundDto == null){
            throw new IllegalArgumentException("AiOutbound object can not be null");
        }
        String leadId = Optional.ofNullable(aiOutboundDto.getLeadId())
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("leadId is empty"));

        String correlationId = Optional.ofNullable(aiOutboundDto.getCorrelationId())
                .map(s -> s.trim().toLowerCase())
                .filter(s-> !s.isEmpty())
                .orElseGet(correlationIDGenerator::generate);

        if (correlationId.length() > 64 || !correlationId.matches(UUID_RE)){
            throw new IllegalArgumentException("Correlation ID is not valid");
        }
        aiOutboundDto.setCorrelationId(correlationId);
        aiOutboundDto.setLeadId(leadId);

        MessagePostProcessor headers = message -> {
            MessageProperties p = message.getMessageProperties();

            p.setContentType(MessageProperties.CONTENT_TYPE_JSON);

            p.setHeader("schemaVersion", "1.0");
            p.setHeader("source", "zoho-service");
            p.setHeader("leadId", leadId);

            p.setCorrelationId(correlationId);

            return message;
        };

        try {
            amqpTemplate.convertAndSend(exchange, routingKey, aiOutboundDto, headers);
            return correlationId;
        } catch (AmqpException e) {
            throw e;
        }
    }
}
