package com.fiap.midasfintech.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_audit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SecurityAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(length = 100)
    private String username;

    @Column(length = 100)
    private String sourceIp;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
