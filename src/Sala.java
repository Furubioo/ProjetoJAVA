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

    public void adicionarSessao(Sessao sessao, int horario){
        if(horario >= 0 && horario < listaSessoes.length){
            this.listaSessoes[horario] = sessao;
        }

        else{
            System.out.println("Horário inválido para esta sala.");
        }


    }
    
    
}
