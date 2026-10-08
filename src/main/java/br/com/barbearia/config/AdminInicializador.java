package br.com.barbearia.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import br.com.barbearia.model.Perfil;
import br.com.barbearia.service.UsuarioService;

@Component
public class AdminInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicializador.class);

    private final UsuarioService usuarioService;
    private final String email;
    private final String senha;

    public AdminInicializador(
            UsuarioService usuarioService,
            @Value("${barbearia.admin.email:}") String email,
            @Value("${barbearia.admin.senha:}") String senha) {
        this.usuarioService = usuarioService;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioService.existeAdministrador()) {
            return;
        }
        if (!StringUtils.hasText(email) || !StringUtils.hasText(senha)) {
            log.warn("Nenhum administrador cadastrado. Defina ADMIN_EMAIL e ADMIN_SENHA para criar o primeiro.");
            return;
        }
        usuarioService.cadastrar("Administrador", email, "(00) 00000-0000", senha, Perfil.ADMIN);
        log.info("Administrador inicial criado: {}", email);
    }
}
