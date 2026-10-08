package br.com.barbearia.tema;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "barbearia.tema")
public record TemaProperties(
        @DefaultValue("classico") String nome,
        @DefaultValue("padrao") String layout,
        @DefaultValue("Navalha & Cia") String nomeBarbearia,
        @DefaultValue("Tradição e estilo desde 1998") String slogan) {
}
