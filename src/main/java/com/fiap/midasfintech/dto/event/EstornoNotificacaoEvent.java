package com.fiap.midasfintech.dto.event;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstornoNotificacaoEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long contaId;
    private String contaNome;
    private String emailNotificacao;
    private String telefoneSms;
    private Long transacaoOriginalId;
    private Long transacaoEstornoId;
    private BigDecimal valor;
    private String motivo;
    private LocalDateTime dataEstorno;

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public String getContaNome() {
        return contaNome;
    }

    public void setContaNome(String contaNome) {
        this.contaNome = contaNome;
    }

    public Long getTransacaoOriginalId() {
        return transacaoOriginalId;
    }

    public String getEmailNotificacao() {
        return emailNotificacao;
    }

    public void setEmailNotificacao(String emailNotificacao) {
        this.emailNotificacao = emailNotificacao;
    }

    public String getTelefoneSms() {
        return telefoneSms;
    }

    public void setTelefoneSms(String telefoneSms) {
        this.telefoneSms = telefoneSms;
    }

    public void setTransacaoOriginalId(Long transacaoOriginalId) {
        this.transacaoOriginalId = transacaoOriginalId;
    }

    public Long getTransacaoEstornoId() {
        return transacaoEstornoId;
    }

    public void setTransacaoEstornoId(Long transacaoEstornoId) {
        this.transacaoEstornoId = transacaoEstornoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(LocalDateTime dataEstorno) {
        this.dataEstorno = dataEstorno;
    }
}
