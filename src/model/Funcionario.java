package model;

public class Funcionario extends Base implements GerenciaDeFilmes {

    private double salario;
    private Filme[] catalogoFilmes;
    private int qtdFilmes;
    private static final int MAX = 100;

    public Funcionario(String nome, int idade, String email, double salario) {
        super(nome, idade, email);
        this.salario = salario;
        this.catalogoFilmes = new Filme[MAX];
        this.qtdFilmes = 0;
    }

    public double getSalario() { 
        return salario;
    }
    public void setSalario(double salario) {
        this.salario = salario; 
    }

    @Override
    public void adicionarUsuario() {
        System.out.println("Funcionário adicionado: " + toString());
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
        System.out.println("Funcionário atualizado: " + toString());
    }

    @Override
    public void incluirFilme(Filme filme) {
        if (qtdFilmes < MAX) {
            catalogoFilmes[qtdFilmes++] = filme;
            System.out.println("Filme incluído: " + filme.getNome());
        }
    }

    @Override
    public void excluirFilme(String nomeFilme) {
        for (int i = 0; i < qtdFilmes; i++) {
            if (catalogoFilmes[i].getNome().equalsIgnoreCase(nomeFilme)) {
                for (int j = i; j < qtdFilmes - 1; j++) 
                    catalogoFilmes[j] = catalogoFilmes[j + 1];
                    catalogoFilmes[--qtdFilmes] = null;
                    System.out.println("Filme removido: " + nomeFilme);
                    return;
            }
        }
        System.out.println("Filme não encontrado: " + nomeFilme);
    }

    @Override
    public void alterarFilme(String nomeFilme, Filme novosDados) {
        for (int i = 0; i < qtdFilmes; i++) {
            if (catalogoFilmes[i].getNome().equalsIgnoreCase(nomeFilme)) {
                catalogoFilmes[i] = novosDados;
                System.out.println("Filme alterado: " + nomeFilme);
                return;
            }
        }
        System.out.println("Filme não encontrado: " + nomeFilme);
    }

    @Override
    public String toString() {
        return super.toString() + " | Salário: R$ " + String.format("%.2f", salario) + " [Funcionário]";
    }
}