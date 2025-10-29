package com.zoho.getzoho.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiOutboundDto {
    private String correlationId;
    private String leadId;
    private String name;
    private String phone;
    private String email;
}
