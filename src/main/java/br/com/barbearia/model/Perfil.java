package br.com.barbearia.model;

public enum Perfil {

    CLIENTE("Cliente", "/cliente"),
    BARBEIRO("Barbeiro", "/barbeiro"),
    ADMIN("Administrador", "/admin");

    private final String descricao;
    private final String paginaInicial;

    Perfil(String descricao, String paginaInicial) {
        this.descricao = descricao;
        this.paginaInicial = paginaInicial;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPaginaInicial() {
        return paginaInicial;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
