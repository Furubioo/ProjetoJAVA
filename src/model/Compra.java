package model;

public class Compra {
    private Produto[] produtos;
    private double[] precosProdutos;
    private int qtdProdutos;
    private Bilhete[] bilhetes;
    private int qtdBilhetes;

    private static final int MAX_ITENS = 15;

    public Compra() {
        this.produtos = new Produto[MAX_ITENS];
        this.precosProdutos = new double[MAX_ITENS];
        this.qtdProdutos = 0;
        this.bilhetes = new Bilhete[MAX_ITENS];
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
        adicionarProduto(produto, CupomPromocional.NENHUM);
    }

    public void adicionarProduto(Produto produto, CupomPromocional cupom) {
        if (qtdProdutos >= produtos.length) {
            System.out.println("Não é mais possível adicionar produtos.");
            return;
        }
        double precoFinal = produto.getPreco() * (1 - cupom.getDesconto());
        produtos[qtdProdutos] = produto;
        precosProdutos[qtdProdutos] = precoFinal;
        qtdProdutos++;
    }

    public void comprarBilhetes(Bilhete novoBilhete) {
        adicionarBilhete(novoBilhete);
    }

    public void comprarBilhetes(Bilhete novoBilhete, CupomPromocional cupom) {
        novoBilhete.setValor(novoBilhete.getValor() * (1.0 - cupom.getDesconto()));
        adicionarBilhete(novoBilhete);
    }

    private void adicionarBilhete(Bilhete novoBilhete) {
        if (qtdBilhetes >= bilhetes.length) {
            throw new IllegalStateException("Limite máximo de bilhetes por compra atingido (" + bilhetes.length + ").");
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