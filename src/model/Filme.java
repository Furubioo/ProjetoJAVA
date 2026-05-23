package model;

public class Filme {
    private String nome;
    private int duracao;
    private String sinopse;
    private double valor;
    private double nota;
    private int quantidade_criticos;
    private Critica critica;
    private Critica[] vetorcriticas = new Critica[100];

    public Filme(String nome, int duracao, String sinopse, double valor) {
        this.nome = nome;
        this.duracao = duracao;
        this.sinopse = sinopse;
        this.valor = valor;
        this.nota = 0;
        this.quantidade_criticos = 0;
        this.critica = new Critica(nome, "sistema", "Mural inicial");
    }

    public String getNome() { 
        return nome; 
    }

    public void setNome(String nome) { 
        this.nome = nome; 
    }

    public int getDuracao() {
        return duracao;  
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao; 
    }

    public String getSinopse() { 
        return sinopse;
    }
    public void setSinopse(String sinopse) { 
        this.sinopse = sinopse; 
    }

    public double getValor() { 
        return valor; 
    }

    public void setValor(double valor) {
        this.valor = valor; 
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) { 
        this.nota = nota;
    }

    public int getQuantidade_criticos() { 
        return quantidade_criticos;
    }
    public void setQuantidade_criticos(int quantidade_criticos) {
        this.quantidade_criticos = quantidade_criticos; 
    }

    public Critica getCritica() {
        return critica;
    }
    public void setCritica(Critica critica) { 
        this.critica = critica; 
    }

    public Critica[] getVetorcriticas() {
        return vetorcriticas; 
    }
    public void setVetorcriticas(Critica[] vetorcriticas) {
        this.vetorcriticas = vetorcriticas;
    }

    @Override
    public String toString() {
        return nome + " | " + duracao + "min | R$ " + String.format("%.2f", valor) + " | Nota: " + String.format("%.1f", nota);
    }
}