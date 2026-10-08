package br.com.barbearia.model;

import java.math.BigDecimal;

public record Servico(String nome, String descricao, int duracaoMinutos, BigDecimal preco) {
}
