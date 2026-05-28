package com.cinema.backend.model;

import com.cinema.backend.model.strategy.PrecoCritico;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CRITICO")
public class Critico extends Usuario {

    private String origem;

    public Critico() {
        super();
        setEstrategiaPreco(new PrecoCritico());
    }

    public Critico(String user, String cpf, String senha, int idade, char sexo,
                   String email, String nomeCartao, String numeroCartao,
                   String codigoCartao, String origem) {
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao, new PrecoCritico());
        this.origem = origem;
    }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public void atribuirNota(Filme filme, double nota) {
        if (nota < 0 || nota > 10) return;

        double somaAtual = filme.getNota() * filme.getQuantidadeCriticos();
        filme.setQuantidadeCriticos(filme.getQuantidadeCriticos() + 1);
        filme.setNota((somaAtual + nota) / filme.getQuantidadeCriticos());
    }

    public Critica atribuirCritica(Filme filme, String mensagem) {
        Critica critica = new Critica(getUser(), origem, mensagem, filme);
        filme.adicionarCritica(critica); // aloca no final da lista do Filme
        return critica;
    }

    @Override
    public double comprarBilhete(double valorBase) {
        return 0.0; 
    }
}