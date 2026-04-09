package com.fiap.midasfintech.controller;

import com.fiap.midasfintech.config.SecurityProperties;
import com.fiap.midasfintech.dto.request.EstornoRequestDto;
import com.fiap.midasfintech.dto.request.TransferenciaRequestDto;
import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.service.AccountService;
import com.fiap.midasfintech.service.FluxoFinanceiroService;
import com.fiap.midasfintech.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AreaController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final FluxoFinanceiroService fluxoFinanceiroService;
    private final SecurityProperties securityProperties;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("githubEnabled", securityProperties.getOauth2().isGithubEnabled());
        return "login";
    }

    @GetMapping("/cliente/dashboard")
    public String dashboardCliente(Model model) {
        model.addAttribute("contas", accountService.findAll());
        return "cliente-dashboard";
    }

    @GetMapping("/cliente/transferencia")
    public String formularioTransferencia(Model model) {
        if (!model.containsAttribute("transferenciaForm")) {
            model.addAttribute("transferenciaForm", new TransferenciaRequestDto());
        }
        model.addAttribute("contas", accountService.findAll());
        return "transferencia-form";
    }

    @PostMapping("/cliente/transferencia")
    public String realizarTransferencia(
            @Valid @ModelAttribute("transferenciaForm") TransferenciaRequestDto transferenciaForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.transferenciaForm",
                    bindingResult);
            redirectAttributes.addFlashAttribute("transferenciaForm", transferenciaForm);
            return "redirect:/cliente/transferencia";
        }

        try {
            fluxoFinanceiroService.realizarTransferencia(
                    transferenciaForm.getContaOrigemId(),
                    transferenciaForm.getContaDestinoId(),
                    transferenciaForm.getValor(),
                    transferenciaForm.getDescricao());
            redirectAttributes.addFlashAttribute("success", "Transferencia realizada com sucesso.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("transferenciaForm", transferenciaForm);
        }

        return "redirect:/cliente/transferencia";
    }

    @GetMapping("/admin/painel")
    public String painelAdmin(Model model) {
        if (!model.containsAttribute("estornoForm")) {
            model.addAttribute("estornoForm", new EstornoRequestDto());
        }

        List<Transaction> transacoes = transactionService.findAll().stream()
                .sorted(Comparator.comparing(Transaction::getData).reversed())
                .limit(30)
                .toList();

        model.addAttribute("transacoes", transacoes);
        return "admin-painel";
    }

    @PostMapping("/admin/estorno")
    public String estornarTransacao(
            @Valid @ModelAttribute("estornoForm") EstornoRequestDto estornoForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.estornoForm",
                    bindingResult);
            redirectAttributes.addFlashAttribute("estornoForm", estornoForm);
            return "redirect:/admin/painel";
        }

        try {
            fluxoFinanceiroService.estornarTransacao(estornoForm.getTransacaoId(), estornoForm.getMotivo());
            redirectAttributes.addFlashAttribute("success", "Estorno executado com sucesso.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/painel";
    }
}
