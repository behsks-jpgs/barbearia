package br.com.barbearia.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.barbearia.model.Perfil;
import br.com.barbearia.service.CatalogoServicos;

@Controller
public class HomeController {

    private final CatalogoServicos catalogo;

    public HomeController(CatalogoServicos catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("servicos", catalogo.listar());
        return "index";
    }

    @GetMapping("/painel")
    public String painel(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .map(Perfil::valueOf)
                .findFirst()
                .map(perfil -> "redirect:" + perfil.getPaginaInicial())
                .orElse("redirect:/");
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "error/403";
    }
}
