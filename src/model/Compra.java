package model;

public class Compra {
    private Produto[] produtos;
    private int qtdProdutos;
    private Bilhete[] bilhetes;
    private int qtdBilhetes;

    public Compra() {
        this.produtos    = new Produto[15];
        this.qtdProdutos = 0;
        this.bilhetes    = new Bilhete[15];
        this.qtdBilhetes = 0;
    }

    public Produto[] getProdutos() { 
        return produtos; 
    }
    public void setProdutos(Produto[] produtos) {
        this.produtos = produtos;
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
        if (qtdProdutos < produtos.length) {
            produtos[qtdProdutos++] = produto;
        } else {
            System.out.println("Não é mais possível adicionar produtos.");
        }
    }

    public void adicionarProduto(Produto produto, CupomPromocional cupom) {
        if (qtdProdutos < produtos.length) {
            produtos[qtdProdutos++] = produto;
            System.out.printf("Produto: %s | Desconto: %d%% | Valor final: R$ %.2f%n",
                    produto.getNome(),
                    (int)(cupom.getDesconto() * 100),
                    produto.getPreco() * (1 - cupom.getDesconto()));
        } else {
            System.out.println("Não é mais possível adicionar produtos.");
        }
    }

    public void comprarBilhetes(Bilhete novoBilhete) {
        if (qtdBilhetes < bilhetes.length) {
            bilhetes[qtdBilhetes++] = novoBilhete;
        } else {
            System.out.println("Não é mais possível comprar bilhetes.");
        }
    }

    public void comprarBilhetes(Bilhete novoBilhete, CupomPromocional cupom) {
        if (qtdBilhetes < bilhetes.length) {
            novoBilhete.setValor(novoBilhete.getValor() * (1 - cupom.getDesconto()));
            bilhetes[qtdBilhetes++] = novoBilhete;
        } else {
            System.out.println("Não é mais possível comprar bilhetes.");
        }
    }

    public double calcularTotal() {
        double total = 0;
        for (int i = 0; i < qtdBilhetes; i++) total += bilhetes[i].getValor();
        for (int i = 0; i < qtdProdutos; i++) total += produtos[i].getPreco();
        return total;
    }
}