package com.fiap.midasfintech.messaging;

import com.fiap.midasfintech.dto.event.EstornoNotificacaoEvent;
import com.fiap.midasfintech.dto.request.NotificacaoCanalRequestDto;
import com.fiap.midasfintech.integration.NotificacaoFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class EstornoNotificacaoConsumer {

    private final NotificacaoFeignClient notificacaoFeignClient;

    @JmsListener(destination = "${midas.messaging.estorno-queue:estorno.notificacao.queue}")
    public void consumirEventoEstorno(EstornoNotificacaoEvent event) {
        String mensagem = "Estorno concluido da transacao " + event.getTransacaoOriginalId()
                + " no valor de " + event.getValor() + ". Motivo: " + event.getMotivo();

        NotificacaoCanalRequestDto email = new NotificacaoCanalRequestDto();
        email.setDestinatario(event.getEmailNotificacao());
        email.setMensagem(mensagem);

        NotificacaoCanalRequestDto sms = new NotificacaoCanalRequestDto();
        sms.setDestinatario(event.getTelefoneSms());
        sms.setMensagem(mensagem);

        try {
            boolean enviouAlgo = false;

            if (StringUtils.hasText(event.getEmailNotificacao())) {
                notificacaoFeignClient.enviarEmail(email);
                enviouAlgo = true;
            }

            if (StringUtils.hasText(event.getTelefoneSms())) {
                notificacaoFeignClient.enviarSms(sms);
                enviouAlgo = true;
            }

            if (enviouAlgo) {
                log.info("Notificacoes enviadas para conta {} apos estorno {}", event.getContaId(),
                        event.getTransacaoEstornoId());
            } else {
                log.warn("Conta {} sem email/sms para notificacao apos estorno {}",
                        event.getContaId(), event.getTransacaoEstornoId());
            }
        } catch (Exception ex) {
            log.error("Falha ao enviar notificacoes para conta {} apos estorno {}",
                    event.getContaId(), event.getTransacaoEstornoId(), ex);
        }
    }
}
