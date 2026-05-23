package model;

public class Sala {

    public static final String[] HORARIOS = {
        "08:00", "10:00", "12:00", "14:00",
        "16:00", "18:00", "20:00", "22:00"
    };

    private static final int TOTAL_SESSOES = HORARIOS.length;

    private Sessao[] listaSessoes;
    private TipoSala tipo;

    public Sala(TipoSala tipo) {
        this.listaSessoes = new Sessao[TOTAL_SESSOES];
        this.tipo = tipo;
    }

    public Sessao[] getListaSessoes() {
        return listaSessoes;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public void setTipo(TipoSala tipo) {
        this.tipo = tipo;
    }

    public double calcularValorBilhete(double valorBase) {
        return valorBase * tipo.getMultiplicador();
    }

    public void adicionarSessao(Sessao sessao, int indiceHorario) {
        if (indiceHorario >= 0 && indiceHorario < TOTAL_SESSOES) {
            listaSessoes[indiceHorario] = sessao;
        } else {
            System.out.println("Horário inválido para esta sala.");
        }
    }

    public void removerSessao(int indiceHorario) {
        if (indiceHorario >= 0 && indiceHorario < TOTAL_SESSOES) {
            listaSessoes[indiceHorario] = null;
        }
    }

    public Sessao buscarSessao(int indice) {
        if (indice < 0 || indice >= TOTAL_SESSOES) return null;
        return listaSessoes[indice];
    }

    public void listarSessoesDisponiveis() {
        for (int i = 0; i < TOTAL_SESSOES; i++) {
            if (listaSessoes[i] != null) {
                System.out.printf("%d. %s | %s%n", i + 1, HORARIOS[i], listaSessoes[i]);
            }
        }
    }

    @Override
    public String toString() {
        return "Sala [" + tipo.getTipo() + "] | " + TOTAL_SESSOES + " horários";
    }
}