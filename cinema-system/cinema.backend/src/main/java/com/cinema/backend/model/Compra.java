package com.cinema.backend.model;

public class Compra {

    private Produto[] produto;
    private int qtdProdutos;
    private double[] valorProduto;
    private Bilhete[] bilhetes;
    private int qtdBilhetes;

    public Compra() {
        this.produto = new Produto[15];
        this.qtdProdutos = 0;
        this.valorProduto = new double[15];
        this.bilhetes = new Bilhete[15];
        this.qtdBilhetes = 0;
    }

    public Produto[] getProduto() {
        return produto;
    }

    public void setProduto(Produto[] produto) {
        this.produto = produto;
    }

    public int getQtdProdutos() {
        return qtdProdutos;
    }

    public void setQtdProdutos(int qtdProdutos) {
        this.qtdProdutos = qtdProdutos;
    }

    public double[] getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(double[] valorProduto) {
        this.valorProduto = valorProduto;
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
        if (qtdProdutos >= this.produto.length) {
            throw new IllegalStateException(
                    "Limite maximo de produtos por compra atingido (" + this.produto.length + ").");
        }

        this.produto[qtdProdutos] = produto;
        this.valorProduto[qtdProdutos] = produto.getPreco() * (1.0 - cupom.getDesconto());
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
            throw new IllegalStateException("Limite maximo de bilhetes por compra atingido (" + bilhetes.length + ").");
        }

        bilhetes[qtdBilhetes++] = novoBilhete;
    }

    public double calcularTotal() {
        double total = 0;

        for (int i = 0; i < qtdBilhetes; i++) {
            total += bilhetes[i].getValor();
        }

        for (int i = 0; i < qtdProdutos; i++) {
            total += valorProduto[i];
        }

        return total;
    }
}