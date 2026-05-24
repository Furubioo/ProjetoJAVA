package model;

public class Sessao {
    private Filme filme;
    private String horario;
    private boolean[][] cadeiras;
    private boolean emCartaz;

    public Sessao(Filme filme, String horario) {
        this.filme = filme;
        this.horario = horario;
        this.cadeiras = new boolean[10][15];
        this.emCartaz = true;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean[][] getCadeiras() {
        return cadeiras;
    }

    public void setCadeiras(boolean[][] cadeiras) {
        this.cadeiras = cadeiras;
    }

    public boolean isEmCartaz() {
        return emCartaz;
    }

    public void setEmCartaz(boolean emCartaz) {
        this.emCartaz = emCartaz;
    }

    public boolean cadeiraDisponivel(int linha, int coluna) {
        if (!posicaoValida(linha, coluna)) {
            return false;
        }

        return !cadeiras[linha][coluna];
    }

    public void ocuparCadeira(int linha, int coluna) {
        if (posicaoValida(linha, coluna)) {
            cadeiras[linha][coluna] = true;
        }
    }

    public boolean horarioJaPassou() {
        return false; 
    }
    
    public String formatarCadeira(int linha, int coluna) {
        return String.valueOf((char) ('A' + linha)) + (coluna + 1);
    }

    private boolean posicaoValida(int linha, int coluna) {
        return linha >= 0 && linha < cadeiras.length && coluna >= 0 && coluna < cadeiras[linha].length;
    }

    @Override
    public String toString() {
        return "Sessao[" + (filme != null ? filme.getNome() : "sem filme") +
        " | " + horario +
        " | " + (emCartaz ? "em cartaz" : "encerrada") + "]";
    }
}