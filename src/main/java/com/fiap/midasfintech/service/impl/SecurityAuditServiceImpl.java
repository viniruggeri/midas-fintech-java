package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.SecurityAuditEvent;
import com.fiap.midasfintech.repository.SecurityAuditEventRepository;
import com.fiap.midasfintech.service.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SecurityAuditServiceImpl implements SecurityAuditService {

    private final SecurityAuditEventRepository securityAuditEventRepository;

    @Override
    public void registrarEvento(String eventType, String username, String sourceIp, String details, boolean success) {
        SecurityAuditEvent event = new SecurityAuditEvent();
        event.setEventType(eventType);
        event.setUsername(username);
        event.setSourceIp(sourceIp);
        event.setDetails(details);
        event.setSuccess(success);
        event.setCreatedAt(LocalDateTime.now());
        securityAuditEventRepository.save(event);
    }
}
