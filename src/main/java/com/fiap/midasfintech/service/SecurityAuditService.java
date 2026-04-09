package com.fiap.midasfintech.service;

public interface SecurityAuditService {

    void registrarEvento(String eventType, String username, String sourceIp, String details, boolean success);
}
