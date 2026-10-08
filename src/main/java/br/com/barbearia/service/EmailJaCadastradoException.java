package br.com.barbearia.service;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("Já existe uma conta com o e-mail " + email + ".");
    }
}
