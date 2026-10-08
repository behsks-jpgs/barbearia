package br.com.barbearia.controller;

import java.security.Principal;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.barbearia.model.Perfil;
import br.com.barbearia.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("perfis", Perfil.values());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/perfil")
    public String alterarPerfil(
            @PathVariable String id,
            @RequestParam Perfil perfil,
            Principal principal,
            RedirectAttributes redirect) {
        executar(redirect, "Perfil atualizado.",
                () -> usuarioService.alterarPerfil(id, perfil, principal.getName()));
        return "redirect:/admin";
    }

    @PostMapping("/usuarios/{id}/ativo")
    public String alternarAtivo(
            @PathVariable String id,
            Principal principal,
            RedirectAttributes redirect) {
        executar(redirect, "Situação da conta atualizada.",
                () -> usuarioService.alternarAtivo(id, principal.getName()));
        return "redirect:/admin";
    }

    private void executar(RedirectAttributes redirect, String mensagemSucesso, Runnable acao) {
        try {
            acao.run();
            redirect.addFlashAttribute("sucesso", mensagemSucesso);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
    }
}
