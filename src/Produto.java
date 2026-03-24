public enum Produto {
    PIPOCA_PEQUENA(10.0),
    PIPOCA_GRANDE(15.0),
    REFRIGERANTE_300ML(8.0),
    REFRIGERANTE_500ML(10.0);

    private double preco;

    Produto(double preco){
        this.preco = preco;
    }

    public double getPreco(){
        return preco;
    }
}