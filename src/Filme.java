public class Filme {
    private String nome;
    private int duracao;
    private String sinopse;
    private double valor;
    private double nota;
    private int quantidade_criticos;
    private String critica;
    
    
    public Filme(String nome, int duracao, String sinopse, double valor, double nota, int quantidade_criticos, String critica) {
        this.nome = nome;
        this.duracao = duracao;
        this.sinopse = sinopse;
        this.valor = valor;
        this.nota = nota;
        this.quantidade_criticos = quantidade_criticos;
        this.critica = critica;
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

    public String getCritica() {
        return critica;
    }

    public void setCritica(String critica) {
        this.critica = critica;
    }




} 
