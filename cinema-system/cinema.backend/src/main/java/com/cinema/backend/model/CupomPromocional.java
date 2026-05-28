package com.cinema.backend.model;

public enum CupomPromocional {
    NENHUM(0.0),
    MEIA(0.5),
    DESCONTO10(0.1),
    DESCONTO20(0.2);

    private double desconto;

    CupomPromocional(double desconto){
        this.desconto = desconto;
    }

    public double getDesconto(){
        return desconto;
    }
}
