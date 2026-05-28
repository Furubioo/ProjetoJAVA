package com.cinema.backend.model.strategy;

public class PrecoEstudante implements EstrategiaPreco {
    @Override
    public double calcularPreco(double precoBase) {
        return precoBase / 2.0;
    }
}
