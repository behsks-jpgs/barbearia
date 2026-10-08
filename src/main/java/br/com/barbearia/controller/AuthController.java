package br.com.barbearia.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.barbearia.dto.CadastroForm;
import br.com.barbearia.service.EmailJaCadastradoException;
import br.com.barbearia.service.UsuarioService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return estaAutenticado(authentication) ? "redirect:/painel" : "auth/login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Authentication authentication, Model model) {
        if (estaAutenticado(authentication)) {
            return "redirect:/painel";
        }
        model.addAttribute("form", new CadastroForm());
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute("form") CadastroForm form,
            BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "auth/cadastro";
        }
        try {
            usuarioService.cadastrarCliente(form);
        } catch (EmailJaCadastradoException e) {
            result.rejectValue("email", "email.duplicado", e.getMessage());
            return "auth/cadastro";
        }
        redirect.addFlashAttribute("sucesso", "Conta criada! Entre com seu e-mail e senha.");
        return "redirect:/login";
    }

    private boolean estaAutenticado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
