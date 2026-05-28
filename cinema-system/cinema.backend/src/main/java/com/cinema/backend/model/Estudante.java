package com.cinema.backend.model;

import com.cinema.backend.model.strategy.PrecoEstudante;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESTUDANTE")
public class Estudante extends Usuario {

    public Estudante() {
        super();
        setEstrategiaPreco(new PrecoEstudante());
    }

    public Estudante(String user, String cpf, String senha, int idade, char sexo,
                     String email, String nomeCartao, String numeroCartao, String codigoCartao) {
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao, new PrecoEstudante());
    }

    @Override
    public double comprarBilhete(double valorBase) {
        return valorBase / 2.0; 
    }
}