public class Bilhete {
    private Usuario user;
    private int sala;
    private Sessao sessao;
    private Filme filme;
    private double valor;
    private int[][] cadeiras = new int[10][15];  

    public Bilhete(Usuario user, int sala, Sessao sessao, Filme filme, double valor, int[][] cadeiras) {
        this.user = user;
        this.sala = sala;
        this.sessao = sessao;
        this.filme = filme;
        this.valor = valor;
        this.cadeiras = cadeiras;

    }

    public Usuario getUser() {
        return user;
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

    public void setUser(Usuario user) {
        this.user = user;
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

    public String LocalCadeiras(int[][] cadeiras) {

        String c = "";

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                if (cadeiras[i][j] == 1) {
                    char a = (char) ('A' + i);
                    c = a + "" + j;
                }
            }
        }

        return c; 
    }


}
