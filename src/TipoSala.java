public enum TipoSala {
    NORMAL(1.0),
    XD(1.3),
    XD_3D(1.4),
    SALA_3D(1.2);

    private double multiplicador;

    TipoSala(double multiplicador){
        this.multiplicador = multiplicador;
    }

    public double getMultiplicador(){
        return multiplicador;
    }
}
