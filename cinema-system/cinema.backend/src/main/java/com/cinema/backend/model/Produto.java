package com.cinema.backend.model;

public enum Produto {
    PIPOCA_PEQUENA("Pipoca Pequena", 10.0),
    PIPOCA_GRANDE("Pipoca Grande", 15.0),
    REFRIGERANTE_300ML("Refrigerante 300ml", 8.0),
    REFRIGERANTE_500ML("Refrigerante 500ml", 10.0),
    CHOCOLATE("Chocolate", 7.0),
    AGUA_MINERAL("Água Mineral", 5.0);

    private final String nome;
    private final double preco;

    Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome()  { return nome; }
    public double getPreco() { return preco; }
}