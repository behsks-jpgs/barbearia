package br.com.barbearia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.barbearia.dto.CadastroForm;
import br.com.barbearia.model.Perfil;
import br.com.barbearia.model.Usuario;
import br.com.barbearia.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);

    private UsuarioService service;

    @BeforeEach
    void configurar() {
        service = new UsuarioService(repository, encoder);
    }

    @Test
    void cadastraClienteComSenhaEmHashEEmailNormalizado() {
        when(repository.existsByEmail("joao@email.com")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario usuario = service.cadastrarCliente(formulario(" Joao@Email.com ", "Senha@123"));

        assertThat(usuario.getEmail()).isEqualTo("joao@email.com");
        assertThat(usuario.getPerfil()).isEqualTo(Perfil.CLIENTE);
        assertThat(usuario.getSenhaHash()).isNotEqualTo("Senha@123").startsWith("$2a$");
        assertThat(encoder.matches("Senha@123", usuario.getSenhaHash())).isTrue();
    }

    @Test
    void recusaEmailJaCadastrado() {
        when(repository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarCliente(formulario("joao@email.com", "Senha@123")))
                .isInstanceOf(EmailJaCadastradoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void administradorNaoAlteraOProprioPerfil() {
        Usuario admin = new Usuario("Admin", "admin@email.com", "11999999999", "hash", Perfil.ADMIN);
        when(repository.findById("1")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.alterarPerfil("1", Perfil.CLIENTE, "admin@email.com"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());
    }

    private CadastroForm formulario(String email, String senha) {
        CadastroForm form = new CadastroForm();
        form.setNome("João Silva");
        form.setEmail(email);
        form.setTelefone("(11) 91234-5678");
        form.setSenha(senha);
        form.setConfirmacaoSenha(senha);
        return form;
    }
}
