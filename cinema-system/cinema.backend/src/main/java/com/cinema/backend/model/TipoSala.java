package com.cinema.backend.model;

public enum TipoSala {
    COMUM("Comum", 1.0),
    XD("XD", 1.3),
    XD_3D("XD/3D", 1.4),
    SALA_3D("3D", 1.2);

    private final String tipo;
    private final double multiplicador;

    TipoSala(String tipo, double multiplicador) {
        this.tipo = tipo;
        this.multiplicador = multiplicador;
    }

    public String getTipo() {
        return tipo;
    }

    public double getMultiplicador() {
        return multiplicador;
    }

    @Override
    public String toString() {
        return tipo;
    }
}