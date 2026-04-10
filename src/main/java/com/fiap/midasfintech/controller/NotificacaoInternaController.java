package com.fiap.midasfintech.controller;

import com.fiap.midasfintech.dto.request.NotificacaoCanalRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/notificacoes")
public class NotificacaoInternaController {

    @PostMapping("/email")
    public ResponseEntity<Void> enviarEmail(@Valid @RequestBody NotificacaoCanalRequestDto request) {
        log.info("EMAIL enviado para {} | {}", request.getDestinatario(), request.getMensagem());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/sms")
    public ResponseEntity<Void> enviarSms(@Valid @RequestBody NotificacaoCanalRequestDto request) {
        log.info("SMS enviado para {} | {}", request.getDestinatario(), request.getMensagem());
        return ResponseEntity.accepted().build();
    }
}
