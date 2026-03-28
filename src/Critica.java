public class Critica{
    private String nome;
    private String origem;
    private String mensagem;
    private String[] vetorcriticas = new String[100];


    public Critica(String nome, String origem, String mensagem) {
        this.nome = nome;
        this.origem = origem;
        this.mensagem = mensagem;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getOrigem() {
        return origem;
    }
    public void setOrigem(String origem) {
        this.origem = origem;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String[] getVetorcriticas() {
        return vetorcriticas;
    }

    public void setVetorcriticas(String[] vetorcriticas) {
        this.vetorcriticas = vetorcriticas;
    }

    public void atribuirNota(Filme filme, double nota) {
        filme.setNota(nota);
        filme.setQuantidade_criticos(filme.getQuantidade_criticos() + 1);
    }

    public void atribuirCritica(Filme filme, String critica) {
        filme.setCritica(critica);
        for (int i = this.vetorcriticas.length-1; i >= 0; i--) {
            if (this.vetorcriticas[i] == null) {
                this.vetorcriticas[i] = critica;
                break;
            }
        }

    }


    

    
}
