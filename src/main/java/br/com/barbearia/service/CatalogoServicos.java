package br.com.barbearia.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.barbearia.model.Servico;

@Service
public class CatalogoServicos {

    private static final List<Servico> SERVICOS = List.of(
            new Servico("Corte clássico", "Tesoura e máquina, finalizado com navalha.", 30, new BigDecimal("45.00")),
            new Servico("Barba completa", "Toalha quente, navalha e balm hidratante.", 30, new BigDecimal("35.00")),
            new Servico("Corte + barba", "O combo completo da casa.", 60, new BigDecimal("70.00")),
            new Servico("Pezinho", "Acabamento de contorno entre cortes.", 15, new BigDecimal("15.00")));

    public List<Servico> listar() {
        return SERVICOS;
    }
}
