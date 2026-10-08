package br.com.barbearia.service;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.barbearia.dto.CadastroForm;
import br.com.barbearia.model.Perfil;
import br.com.barbearia.model.Usuario;
import br.com.barbearia.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrarCliente(CadastroForm form) {
        return cadastrar(form.getNome(), form.getEmail(), form.getTelefone(), form.getSenha(), Perfil.CLIENTE);
    }

    public Usuario cadastrar(String nome, String email, String telefone, String senha, Perfil perfil) {
        String emailNormalizado = normalizarEmail(email);
        if (repository.existsByEmail(emailNormalizado)) {
            throw new EmailJaCadastradoException(emailNormalizado);
        }
        Usuario usuario = new Usuario(
                nome.trim(),
                emailNormalizado,
                telefone.trim(),
                passwordEncoder.encode(senha),
                perfil);
        try {
            return repository.save(usuario);
        } catch (DuplicateKeyException e) {
            throw new EmailJaCadastradoException(emailNormalizado);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarTodos() {
        return repository.findAllByOrderByNomeAsc();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void alterarPerfil(String usuarioId, Perfil novoPerfil, String emailSolicitante) {
        Usuario usuario = buscar(usuarioId);
        if (usuario.getEmail().equals(emailSolicitante)) {
            throw new IllegalArgumentException("Você não pode alterar o próprio perfil.");
        }
        usuario.setPerfil(novoPerfil);
        repository.save(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void alternarAtivo(String usuarioId, String emailSolicitante) {
        Usuario usuario = buscar(usuarioId);
        if (usuario.getEmail().equals(emailSolicitante)) {
            throw new IllegalArgumentException("Você não pode desativar a própria conta.");
        }
        usuario.setAtivo(!usuario.isAtivo());
        repository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(normalizarEmail(email))
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));
    }

    public boolean existeAdministrador() {
        return repository.existsByPerfil(Perfil.ADMIN);
    }

    private Usuario buscar(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
