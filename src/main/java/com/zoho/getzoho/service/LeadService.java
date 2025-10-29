package com.zoho.getzoho.service;

import com.zoho.getzoho.dto.AiOutboundDto;
import com.zoho.getzoho.dto.IncomingDto;
import com.zoho.getzoho.persistance.LeadStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LeadService {

    private final LeadStorage leadStorage;

    private final AiTaskProducer aiTaskProducer;

    public LeadService(LeadStorage leadStorage, AiTaskProducer aiTaskProducer) {
        this.leadStorage = leadStorage;
        this.aiTaskProducer = aiTaskProducer;
    }

    public Collection<IncomingDto> listLeads(){
        return leadStorage.findAll();
    }

    public IngestResult saveBatch(List<IncomingDto> batch ){

        int accepted = 0;
        int created = 0;
        int updated = 0;
        int skippedOlder = 0;
        int errors = 0;
        int duplicatesInBatch = 0;

        if (batch == null || batch.size() == 0 || batch.isEmpty()) {
            return new IngestResult(0,0,0,0,0,0);
        }

        Set<String> dedup = new HashSet<>();

        for (IncomingDto i : batch) {

            String id = i.getId() == null ? "" : i.getId().trim();
            String email = i.getEmail() == null ? "" : i.getEmail().trim().toLowerCase();
            String phone = i.getPhone() == null ? "" : i.getPhone().trim();

            if (id.isEmpty() && email.isEmpty() && phone.isEmpty()) {
                errors++;
                continue;
            }

            String key = !id.isEmpty() ? id : (!email.isEmpty() ? email : phone);

            if (!dedup.add(key))
            {
                duplicatesInBatch++;
                continue;
            }

            if (leadStorage.containsKey(key)) {
                updated++;
            } else {
                created++;
            }
            accepted++;
            leadStorage.saveOrUpdate(key, i);
            AiOutboundDto out = new AiOutboundDto();
            out.setLeadId(key);
            out.setName(i.getName());
            out.setPhone(i.getPhone());
            out.setEmail(i.getEmail());
            try {
                aiTaskProducer.sendTask(out);
            } catch (RuntimeException ex) {
                errors++;
            }

        }
        return new IngestResult(accepted, created, updated, skippedOlder, errors, duplicatesInBatch);
    }

}

