package model;

import model.strategy.PrecoCritico;

public class Critico extends Usuario {
    private String origem;

    public Critico(String user, String cpf, String senha, int idade, char sexo, String email,
        String nomeCartao, String numeroCartao, String codigoCartao, String origem) {
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao, new PrecoCritico());
        this.origem = origem;
    }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public void atribuirNota(Filme filme, double nota) {
        if (nota < 0 || nota > 10) {
            System.out.println("Nota invalida. Informe uma nota entre 0 e 10.");
            return;
        }

        double somaAtual = filme.getNota() * filme.getQuantidade_criticos();
        filme.setQuantidade_criticos(filme.getQuantidade_criticos() + 1);
        filme.setNota((somaAtual + nota) / filme.getQuantidade_criticos());
    }

    public void atribuirCritica(String critica, Filme filme) {
        Critica[] criticas = filme.getVetorcriticas();

        for (int i = 0; i < criticas.length; i++) {
            if (criticas[i] == null) {
                criticas[i] = new Critica(getUser(), origem, critica);
                return;
            }
        }

        System.out.println("Limite de criticas atingido para este filme.");
    }

    public void atribuirCritica(Filme filme, String critica) {
        atribuirCritica(critica, filme);
    }
    
    @Override
    public Bilhete comprarBilhete(Sala sala, Sessao sessao, int linha, int coluna) {
        double valorFinal = 0.0; // entrada gratuita garantida
        return criarBilhete(sala, sessao, linha, coluna, valorFinal);
    }

    @Override
    public String toString() {
        return super.toString() + " [Critico | Origem: " + origem + "]";
    }
}