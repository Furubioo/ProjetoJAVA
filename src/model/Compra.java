package model;

public class Compra {
    private Produto[] produtos;
    private double[] precosProdutos;
    private int qtdProdutos;
    private Bilhete[] bilhetes;
    private int qtdBilhetes;

    public Compra() {
        this.produtos = new Produto[15];
        this.precosProdutos = new double[15];
        this.qtdProdutos = 0;
        this.bilhetes = new Bilhete[15];
        this.qtdBilhetes = 0;
    }

    public Produto[] getProdutos() {
        return produtos;
    }

    public void setProdutos(Produto[] produtos) {
        this.produtos = produtos;
    }

    public double[] getPrecosProdutos() {
        return precosProdutos;
    }

    public void setPrecosProdutos(double[] precosProdutos) {
        this.precosProdutos = precosProdutos;
    }

    public int getQtdProdutos() {
        return qtdProdutos;
    }

    public void setQtdProdutos(int qtdProdutos) {
        this.qtdProdutos = qtdProdutos;
    }

    public Bilhete[] getBilhetes() {
        return bilhetes;
    }

    public void setBilhetes(Bilhete[] bilhetes) {
        this.bilhetes = bilhetes;
    }

    public int getQtdBilhetes() {
        return qtdBilhetes;
    }

    public void setQtdBilhetes(int qtdBilhetes) {
        this.qtdBilhetes = qtdBilhetes;
    }

    public void adicionarProduto(Produto produto) {
        adicionarProdutoComValor(produto, produto.getPreco());
    }

    public void adicionarProduto(Produto produto, CupomPromocional cupom) {
        double precoFinal = produto.getPreco() * (1 - cupom.getDesconto());
        adicionarProdutoComValor(produto, precoFinal);
    }

    private void adicionarProdutoComValor(Produto produto, double precoFinal) {
        if (qtdProdutos >= produtos.length) {
            System.out.println("Não e mais possivel adicionar produtos.");
            return;
        }

        produtos[qtdProdutos] = produto;
        precosProdutos[qtdProdutos] = precoFinal;
        qtdProdutos++;
    }

    public void comprarBilhetes(Bilhete novoBilhete) {
        adicionarBilhete(novoBilhete);
    }

    public void comprarBilhetes(Bilhete novoBilhete, CupomPromocional cupom) {
        novoBilhete.setValor(novoBilhete.getValor() * (1 - cupom.getDesconto()));
        adicionarBilhete(novoBilhete);
    }

    private void adicionarBilhete(Bilhete novoBilhete) {
        if (qtdBilhetes >= bilhetes.length) {
            System.out.println("Nao e mais possivel comprar bilhetes.");
            return;
        }

        bilhetes[qtdBilhetes++] = novoBilhete;
    }

    public double calcularTotal() {
        double total = 0;

        for (int i = 0; i < qtdBilhetes; i++) {
            total += bilhetes[i].getValor();
        }

        for (int i = 0; i < qtdProdutos; i++) {
            total += precosProdutos[i];
        }

        return total;
    }
}