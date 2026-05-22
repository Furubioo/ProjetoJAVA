public class Critico extends Usuario{
    private String origem;

    public Critico(String user, String cpf, String senha, int idade, char sexo, String email, String nomeCartao, String numeroCartao, String codigoCartao, String origem) {
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao);
        this.origem = origem;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    @Override 
    public double calcularPrecoFinal(double precoBase){
        precoBase = 0;
        return precoBase;
    }

    
    public void atribuirNota(Filme filme, double nota) {
        filme.setQuantidade_criticos(filme.getQuantidade_criticos() + 1);
        filme.setNota((filme.getNota() + nota) / filme.getQuantidade_criticos());

    }

    public void atribuirCritica(Filme filme, String critica) {
        String[] vet = filme.getVetorcriticas();
        
        for (int i = vet.length-1; i >= 0; i--) {
            if (vet[i] == null) {
                vet[i] = critica;
                break;
            }
        }
    }


}
