package model;

import model.strategy.PrecoEstudante;

public class Estudante extends Usuario {

    public Estudante(String user, String cpf, String senha, int idade, char sexo, String email,
        String nomeCartao, String numeroCartao, String codigoCartao) {
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao, new PrecoEstudante());
    }

    @Override
    public String toString() {
        return super.toString() + " [Estudante]";
    }

    @Override
    public Bilhete comprarBilhete(Sala sala, Sessao sessao, int linha, int coluna) {
        double valorBase  = sala.calcularValorBilhete(sessao.getFilme().getValor());
        double valorFinal = calcularPrecoFinal(valorBase);
        return criarBilhete(sala, sessao, linha, coluna, valorFinal);
    }
}
