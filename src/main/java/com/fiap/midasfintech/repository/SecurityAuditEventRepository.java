package com.fiap.midasfintech.repository;

import com.fiap.midasfintech.entity.SecurityAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAuditEventRepository extends JpaRepository<SecurityAuditEvent, Long> {
}
