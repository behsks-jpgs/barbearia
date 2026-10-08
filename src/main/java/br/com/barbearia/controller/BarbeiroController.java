package br.com.barbearia.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.barbearia.service.CatalogoServicos;
import br.com.barbearia.service.UsuarioService;

@Controller
@RequestMapping("/barbeiro")
public class BarbeiroController {

    private final UsuarioService usuarioService;
    private final CatalogoServicos catalogo;

    public BarbeiroController(UsuarioService usuarioService, CatalogoServicos catalogo) {
        this.usuarioService = usuarioService;
        this.catalogo = catalogo;
    }

    @GetMapping
    public String agenda(Principal principal, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorEmail(principal.getName()));
        model.addAttribute("hoje", LocalDate.now());
        model.addAttribute("servicos", catalogo.listar());
        return "barbeiro/agenda";
    }
}
