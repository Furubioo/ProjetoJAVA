public class Filme {
    private String nome;
    private int duracao;
    private String sinopse;
    private double valor;

    public Filme(String nome, int duracao, String sinopse, double valor) {
        this.nome = nome;
        this.duracao = duracao;
        this.sinopse = sinopse;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }
    
    public int getDuracao() {
        return duracao;
    }

    public String getSinopse() {
        return sinopse;
    }

    public double getValor() {
        return valor;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

}
