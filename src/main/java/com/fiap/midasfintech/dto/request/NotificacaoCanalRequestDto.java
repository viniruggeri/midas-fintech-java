package com.fiap.midasfintech.dto.request;

import jakarta.validation.constraints.NotBlank;

public class NotificacaoCanalRequestDto {

    @NotBlank
    private String destinatario;

    @NotBlank
    private String mensagem;

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
