package com.fiap.midasfintech.messaging;

import com.fiap.midasfintech.dto.event.EstornoNotificacaoEvent;
import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EstornoNotificacaoPublisher {

    private final JmsTemplate jmsTemplate;

    @Value("${midas.messaging.estorno-queue:estorno.notificacao.queue}")
    private String estornoQueue;

    public void publicarEstornoAprovado(Transaction transacaoOriginal, Transaction transacaoEstorno, Account conta,
            String motivo) {
        EstornoNotificacaoEvent event = new EstornoNotificacaoEvent();
        event.setContaId(conta.getId());
        event.setContaNome(conta.getNome());
        event.setEmailNotificacao(conta.getEmailNotificacao());
        event.setTelefoneSms(conta.getTelefoneSms());
        event.setTransacaoOriginalId(transacaoOriginal.getId());
        event.setTransacaoEstornoId(transacaoEstorno.getId());
        event.setValor(transacaoOriginal.getValor());
        event.setMotivo(motivo.trim());
        event.setDataEstorno(LocalDateTime.now());

        jmsTemplate.convertAndSend(estornoQueue, event);
    }
}
