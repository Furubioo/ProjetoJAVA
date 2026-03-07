public class Bilhete {
    private String user;
    private String cpf;
    private int sala;
    private Sessao sessao;
    private Filme filme;
    private double valor;
    private int[][] cadeiras = new int[10][15];  

    public Bilhete(String user, String cpf, int sala, Sessao sessao, Filme filme, double valor, int[][] cadeiras) {
        this.user = user;
        this.cpf = cpf;
        this.sala = sala;
        this.sessao = sessao;
        this.filme = filme;
        this.valor = valor;
        this.cadeiras = cadeiras;

    }

    public String getUser() {
        return user;
    }

    public String getCpf() {
        return cpf;
    }

    public int getSala() {
        return sala;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public Filme getFilme() {
        return filme;
    }

    public double getValor() {
        return valor;
    }

    public int[][] getCadeiras() {
        return cadeiras;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setSala(int sala) {
        this.sala = sala;
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setCadeiras(int[][] cadeiras) {
        this.cadeiras = cadeiras;
    }



}
