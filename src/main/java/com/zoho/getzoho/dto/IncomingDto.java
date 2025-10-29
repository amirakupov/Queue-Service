package com.zoho.getzoho.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("Lead Source")
    private String leadSource;

    @JsonProperty("Notes")
    private List<String> notes;
}


