package com.zoho.getzoho.persistance;

import com.zoho.getzoho.dto.IncomingDto;
import org.springframework.stereotype.Component;
import java.util.Collection;
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
    public IncomingDto findByKey(String key) {
        if (key == null) return null;
        return incomings.get(key);
    }


}
