package br.com.barbearia.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CadastroFormTest {

    private static ValidatorFactory fabrica;
    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validator = fabrica.getValidator();
    }

    @AfterAll
    static void encerrar() {
        fabrica.close();
    }

    @Test
    void formularioValidoNaoTemErros() {
        assertThat(validator.validate(formulario("Senha@123", "Senha@123"))).isEmpty();
    }

    @Test
    void senhaFracaEhRecusada() {
        assertThat(camposComErro(formulario("12345678", "12345678"))).contains("senha");
    }

    @Test
    void confirmacaoDiferenteEhRecusada() {
        assertThat(camposComErro(formulario("Senha@123", "Senha@124"))).contains("senhaConfirmada");
    }

    @Test
    void emailInvalidoEhRecusado() {
        CadastroForm form = formulario("Senha@123", "Senha@123");
        form.setEmail("nao-e-email");
        assertThat(camposComErro(form)).contains("email");
    }

    private Set<String> camposComErro(CadastroForm form) {
        return validator.validate(form).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    private CadastroForm formulario(String senha, String confirmacao) {
        CadastroForm form = new CadastroForm();
        form.setNome("Maria Souza");
        form.setEmail("maria@email.com");
        form.setTelefone("(11) 98765-4321");
        form.setSenha(senha);
        form.setConfirmacaoSenha(confirmacao);
        return form;
    }
}
