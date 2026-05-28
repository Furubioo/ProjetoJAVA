package com.cinema.backend.model.strategy;

public class PrecoComum implements EstrategiaPreco {
    @Override
    public double calcularPreco(double precoBase) {
        return precoBase;
    }
}
