public class Sala {
    private Sessao[] listaSessoes;
    private TipoSala tipo;

    public Sala(int totalSessoes, TipoSala tipo) {
        this.listaSessoes = new Sessao[totalSessoes];
        this.tipo = tipo;
    }

    public Sessao[] getSessao() {
        return listaSessoes;
    }

    public void setSessao(Sessao[] listaSessoes) {
        this.listaSessoes = listaSessoes;
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

    public void adicionarSessao(Sessao sessao, int horario) {
        if (horario >= 0 && horario < listaSessoes.length) {
            this.listaSessoes[horario] = sessao;
        } else {
            System.out.println("Horário inválido para esta sala.");
        }
    }

    @Override
    public String toString() {
        return "Sala [" + tipo.getTipo() + "] | Sessões: " + listaSessoes.length;
    }
}