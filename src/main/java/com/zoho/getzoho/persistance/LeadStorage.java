package com.zoho.getzoho.persistance;

import com.zoho.getzoho.dto.IncomingDto;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LeadStorage {

    private final ConcurrentHashMap<String, IncomingDto> incomings = new ConcurrentHashMap<>();

    public boolean containsKey(String key){
        return incomings.containsKey(key);
    }

    public void saveOrUpdate(String key, IncomingDto dto) {
        incomings.put(key, dto);
    }
    public Collection<IncomingDto> findAll() {
        return incomings.values();
    }


}
