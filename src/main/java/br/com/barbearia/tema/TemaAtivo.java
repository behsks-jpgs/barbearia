package br.com.barbearia.tema;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component("tema")
public class TemaAtivo {

    private static final Pattern NOME_SEGURO = Pattern.compile("^[a-z0-9-]+$");

    private final TemaProperties properties;

    public TemaAtivo(TemaProperties properties) {
        this.properties = properties;
        validar(properties.nome(), "barbearia.tema.nome");
        validar(properties.layout(), "barbearia.tema.layout");
    }

    public String getNome() {
        return properties.nome();
    }

    public String getCss() {
        return "/temas/" + properties.nome() + "/tema.css";
    }

    public String getLayout() {
        return "layouts/" + properties.layout();
    }

    public String getNomeBarbearia() {
        return properties.nomeBarbearia();
    }

    public String getSlogan() {
        return properties.slogan();
    }

    private static void validar(String valor, String propriedade) {
        if (valor == null || !NOME_SEGURO.matcher(valor).matches()) {
            throw new IllegalStateException(
                    propriedade + " deve conter apenas letras minúsculas, números e hífen.");
        }
    }
}
