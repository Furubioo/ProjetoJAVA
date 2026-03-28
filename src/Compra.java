public class Compra {
    private String[] produto;
    private int qtdProdutos;
    private double[] valorProduto;
    private Bilhete[] bilhetes;
    private int qtdBilhetes;

    public Compra() {
        this.produto = new String[15];
        this.qtdProdutos = 0;
        this.valorProduto = new double[15];
        this.bilhetes = new Bilhete[15];
        this.qtdBilhetes = 0;
    }

    public String[] getProduto() {
        return produto;
    }

    public int getQtdProdutos() {
        return qtdProdutos;
    }

    public double[] getValorProduto(){
        return valorProduto;
    }

    public Bilhete[] getBilhetes() {
        return bilhetes;
    }

    public int getQtdbilhetes() {
        return qtdBilhetes;
    }

    public void setProduto(String[] produto) {
        this.produto = produto;
    }

    public void setQtdProdutos(int qtdprodutos) {
        this.qtdProdutos = qtdprodutos;
    }

    public void setValorProduto(double[] valorProduto){
        this.valorProduto = valorProduto;
    }

    public void setBilhetes(Bilhete[] bilhetes) {
        this.bilhetes = bilhetes;
    }

    public void setQtdbilhetes(int qtdbilhetes) {
        this.qtdBilhetes = qtdbilhetes;
    }

    public void comprarBilhetes(Bilhete novoBilhete) {

        if (qtdBilhetes < 15) {
            this.bilhetes[qtdBilhetes] = novoBilhete;
            this.qtdBilhetes++;
            System.out.println("Bilhete(s) comprado(s): ");
        }
        else {
            System.out.println("Não é mais possível comprar bilhetes");
        }

    }

    public void adicionarProduto(String produto, double valor) {
        if (qtdProdutos < 15) {
            this.produto[qtdProdutos] = produto;
            this.valorProduto[qtdProdutos] = valor;
            this.qtdProdutos++;
        }
        else{
            System.out.println("Não é mais possivel comprar produtos.");
        }
    }

    public void comprarBilhetes(Bilhete novoBilhete,CupomPromocional cupom){
        
        if (qtdBilhetes < 15) {
            novoBilhete.setValor(novoBilhete.getValor() - novoBilhete.getValor() * cupom.getDesconto());
            this.bilhetes[qtdBilhetes] = novoBilhete;
            this.qtdBilhetes++;
            System.out.println("Bilhete(s) comprado(s): ");
        }
        else {
            System.out.println("Não é mais possível comprar bilhetes");
        }
    }

    public void adicionarProduto(String produto,double valor,CupomPromocional cupom){
        if (qtdProdutos < 15) {
            this.produto[qtdProdutos] = produto;
            valor = valor - (valor * cupom.getDesconto());
            this.valorProduto[qtdProdutos] = valor;
            this.qtdProdutos++;
        }
        
        else {
            System.out.println("Não é mais possivel comprar produtos.");
        }
    }
}


