package br.com.barbearia.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.barbearia.service.CatalogoServicos;
import br.com.barbearia.service.UsuarioService;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final UsuarioService usuarioService;
    private final CatalogoServicos catalogo;

    public ClienteController(UsuarioService usuarioService, CatalogoServicos catalogo) {
        this.usuarioService = usuarioService;
        this.catalogo = catalogo;
    }

    @GetMapping
    public String painel(Principal principal, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorEmail(principal.getName()));
        model.addAttribute("servicos", catalogo.listar());
        return "cliente/painel";
    }
}
