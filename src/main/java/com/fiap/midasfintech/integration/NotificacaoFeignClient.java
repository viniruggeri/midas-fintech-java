package com.fiap.midasfintech.integration;

import com.fiap.midasfintech.dto.request.NotificacaoCanalRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notificacaoClient", url = "${midas.notification.base-url:http://localhost:${server.port:8080}}")
public interface NotificacaoFeignClient {

    @PostMapping("/internal/notificacoes/email")
    void enviarEmail(@RequestBody NotificacaoCanalRequestDto request);

    @PostMapping("/internal/notificacoes/sms")
    void enviarSms(@RequestBody NotificacaoCanalRequestDto request);
}
