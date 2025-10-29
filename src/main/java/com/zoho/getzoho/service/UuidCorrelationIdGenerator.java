package com.zoho.getzoho.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class UuidCorrelationIdGenerator implements CorrelationIDGenerator{

    @Value("${correlation.prefix:zoho}")
    private String rawPrefix;

    private static final String regex = "^[a-z0-9]*$";
    private static final String UUID_RE = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";

    @Override
    public String generate() {
        String result = "";
        String uuid = UUID.randomUUID().toString().toLowerCase();
        String prefix = (rawPrefix == null ? "" : rawPrefix).trim().toLowerCase();
        if (!prefix.matches(regex)) {
            System.out.println("warn");
            prefix = "";
        }
        if (prefix.isEmpty()){
            result = uuid;
            if (result.matches(UUID_RE) && result.length() <= 64) {
                return result;
            } else {
                throw new IllegalStateException("UUID correlation id generation failed");
            }
        } else if (!prefix.isEmpty()) {
            result = prefix + "-" + uuid;
        }
        return result;

    }
}
