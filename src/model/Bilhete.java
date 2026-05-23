package model;

public class Bilhete {
    private Usuario user;
    private String cpf;
    private Sala sala;
    private Sessao sessao;
    private Filme filme;
    private double valor;
    private String cadeira;

    public Bilhete(Usuario user, Sala sala, Sessao sessao, Filme filme, double valor, String cadeira) {
        this.user    = user;
        this.cpf     = user.getCpf();
        this.sala    = sala;
        this.sessao  = sessao;
        this.filme   = filme;
        this.valor   = valor;
        this.cadeira = cadeira;
    }

    public Usuario getUser() { 
        return user;
    }
    public void setUser(Usuario user) {
        this.user = user; this.cpf = user.getCpf(); 
    }

    public String getCpf() {
        return cpf; 
    }

    public void setCpf(String cpf) {
        this.cpf = cpf; 
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala; 
    }

    public Sessao getSessao() {
        return sessao; 
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao; 
    }

    public Filme getFilme() {
        return filme; 
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public double getValor() {
        return valor; 
    }

    public void setValor(double valor) { 
        this.valor = valor; 
    }

    public String getCadeira() {
        return cadeira; 
    }

    public void setCadeira(String cadeira) {
        this.cadeira = cadeira; 
    }

    @Override
    public String toString() {
        return "Bilhete[" + filme.getNome() +
               " | Sala: " + sala.getTipo().getTipo() +
               " | Sessão: " + sessao.getHorario() +
               " | Cadeira: " + cadeira +
               " | CPF: " + cpf +
               " | Valor: R$ " + String.format("%.2f", valor) + "]";
    }
}