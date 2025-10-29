package com.zoho.getzoho.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiInboundDto {
    private String correlationId;
    private String leadId;
    private String callId;
    private String scheduledAt;
    private String summary;
    private long durationSec;
    private int attempts;

    private Status status;

}
