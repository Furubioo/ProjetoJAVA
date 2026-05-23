package model;

public class Critico extends Usuario {
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
    public double calcularPrecoFinal(double precoBase) {
        return 0; 
    }

    public void atribuirNota(Filme filme, double nota) {
        double somaAtual = filme.getNota() * filme.getQuantidade_criticos();
        filme.setQuantidade_criticos(filme.getQuantidade_criticos() + 1);
        filme.setNota((somaAtual + nota) / filme.getQuantidade_criticos());
    }

    public void atribuirCritica(Filme filme, String mensagem) {
        Critica[] vet = filme.getVetorcriticas();
        for (int i = 0; i < vet.length; i++) {
            if (vet[i] == null) {
                vet[i] = new Critica(getUser(), this.origem, mensagem);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + " [Crítico | Origem: " + origem + "]";
    }
}