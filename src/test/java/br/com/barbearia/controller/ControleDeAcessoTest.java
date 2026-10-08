package br.com.barbearia.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.barbearia.config.SecurityConfig;
import br.com.barbearia.model.Perfil;
import br.com.barbearia.model.Usuario;
import br.com.barbearia.service.CatalogoServicos;
import br.com.barbearia.service.UsuarioDetailsService;
import br.com.barbearia.service.UsuarioService;
import br.com.barbearia.tema.TemaAtivo;

@WebMvcTest
@Import({SecurityConfig.class, TemaAtivo.class, CatalogoServicos.class})
class ControleDeAcessoTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void paginasPublicasAbremSemLogin() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/cadastro")).andExpect(status().isOk());
    }

    @Test
    void areaRestritaExigeLogin() throws Exception {
        mvc.perform(get("/cliente")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mvc.perform(get("/admin")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "cliente@email.com", roles = "CLIENTE")
    void clienteAcessaSuaAreaMasNaoAsOutras() throws Exception {
        when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuario(Perfil.CLIENTE));

        mvc.perform(get("/cliente")).andExpect(status().isOk());
        mvc.perform(get("/barbeiro")).andExpect(status().isForbidden());
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", roles = "BARBEIRO")
    void barbeiroAcessaAgendaMasNaoAdministracao() throws Exception {
        when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuario(Perfil.BARBEIRO));

        mvc.perform(get("/barbeiro")).andExpect(status().isOk());
        mvc.perform(get("/cliente")).andExpect(status().isForbidden());
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
    void administradorAcessaTodasAsAreas() throws Exception {
        when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuario(Perfil.ADMIN));
        when(usuarioService.listarTodos()).thenReturn(List.of());

        mvc.perform(get("/admin")).andExpect(status().isOk());
        mvc.perform(get("/barbeiro")).andExpect(status().isOk());
        mvc.perform(get("/cliente")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BARBEIRO")
    void painelRedirecionaConformeOPerfil() throws Exception {
        mvc.perform(get("/painel")).andExpect(redirectedUrl("/barbeiro"));
    }

    @Test
    void cadastroSemTokenCsrfEhBloqueado() throws Exception {
        mvc.perform(post("/cadastro").param("nome", "Teste")).andExpect(status().isForbidden());
    }

    @Test
    void cadastroInvalidoVoltaParaOFormulario() throws Exception {
        mvc.perform(post("/cadastro").with(csrf())
                        .param("nome", "A")
                        .param("email", "invalido")
                        .param("telefone", "123")
                        .param("senha", "123")
                        .param("confirmacaoSenha", "456"))
                .andExpect(status().isOk());
    }

    private Usuario usuario(Perfil perfil) {
        return new Usuario("Pessoa Teste", "pessoa@email.com", "(11) 91234-5678", "hash", perfil);
    }
}
