public class Sala {
    private Sessao[] listaSessoes;

    public Sala(int totalSessoes){
        this.listaSessoes = new Sessao[totalSessoes];
    }

    public Sessao[] getSessao(){
        return listaSessoes;
    }

    public void setSessao(Sessao[] listaSessoes){
        this.listaSessoes = listaSessoes;
    }

    
    
}
