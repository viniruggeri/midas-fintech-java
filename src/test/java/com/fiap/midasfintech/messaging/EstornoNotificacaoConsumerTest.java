package com.fiap.midasfintech.messaging;

import com.fiap.midasfintech.dto.event.EstornoNotificacaoEvent;
import com.fiap.midasfintech.integration.NotificacaoFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EstornoNotificacaoConsumerTest {

    @Mock
    private NotificacaoFeignClient notificacaoFeignClient;

    @InjectMocks
    private EstornoNotificacaoConsumer consumer;

    @Test
    void deveEnviarEmailESmsQuandoContaPossuiAmbosCanais() {
        EstornoNotificacaoEvent event = criarEvento();
        event.setEmailNotificacao("cliente@midas.local");
        event.setTelefoneSms("+5511997778888");

        consumer.consumirEventoEstorno(event);

        ArgumentCaptor<com.fiap.midasfintech.dto.request.NotificacaoCanalRequestDto> emailCaptor = ArgumentCaptor
                .forClass(com.fiap.midasfintech.dto.request.NotificacaoCanalRequestDto.class);
        verify(notificacaoFeignClient, times(1)).enviarEmail(emailCaptor.capture());
        assertEquals("cliente@midas.local", emailCaptor.getValue().getDestinatario());

        ArgumentCaptor<com.fiap.midasfintech.dto.request.NotificacaoCanalRequestDto> smsCaptor = ArgumentCaptor
                .forClass(com.fiap.midasfintech.dto.request.NotificacaoCanalRequestDto.class);
        verify(notificacaoFeignClient, times(1)).enviarSms(smsCaptor.capture());
        assertEquals("+5511997778888", smsCaptor.getValue().getDestinatario());
    }

    @Test
    void naoDeveEnviarQuandoContaNaoPossuiContato() {
        EstornoNotificacaoEvent event = criarEvento();

        consumer.consumirEventoEstorno(event);

        verify(notificacaoFeignClient, never()).enviarEmail(org.mockito.ArgumentMatchers.any());
        verify(notificacaoFeignClient, never()).enviarSms(org.mockito.ArgumentMatchers.any());
    }

    private EstornoNotificacaoEvent criarEvento() {
        EstornoNotificacaoEvent event = new EstornoNotificacaoEvent();
        event.setContaId(1L);
        event.setContaNome("Conta Teste");
        event.setTransacaoOriginalId(20L);
        event.setTransacaoEstornoId(21L);
        event.setValor(new BigDecimal("150.00"));
        event.setMotivo("Ajuste operacional");
        return event;
    }
}
