package model;

public enum CupomPromocional {
    NENHUM(0.0),
    DESCONTO10(0.1),
    DESCONTO20(0.2),
    MEIA(0.5);

    private final double desconto;

    CupomPromocional(double desconto) { 
        this.desconto = desconto; 
    }

    public double getDesconto() { 
        return desconto; 
    }

}