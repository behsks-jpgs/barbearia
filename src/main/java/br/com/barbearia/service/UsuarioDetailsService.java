package br.com.barbearia.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.barbearia.model.Usuario;
import br.com.barbearia.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = repository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .authorities(List.of(new SimpleGrantedAuthority(usuario.getPerfil().getAuthority())))
                .disabled(!usuario.isAtivo())
                .build();
    }
}
