package model;

public class Funcionario extends Base implements GerenciaDeFilmes {

    private double salario;

    public Funcionario(String nome, int idade, String email, double salario) {
        super(nome, idade, email);
        this.salario = salario;
    }

    public double getSalario() { 
        return salario; 
    }
    public void setSalario(double salario) { 
        this.salario = salario; 
    }

    @Override
    public void adicionarUsuario(Base usuario) {
        System.out.println("[Funcionário] Solicitação de adição registrada: " + usuario.getNome());
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
        System.out.println("[Funcionário] Dados atualizados: " + toString());
    }

    @Override
    public void incluirFilme(Filme filme) {
        System.out.println("[Funcionário] Solicitado inclusão do filme: " + filme.getNome());
    }

    @Override
    public void excluirFilme(String nomeFilme) {
        System.out.println("[Funcionário] Solicitado exclusão do filme: " + nomeFilme);
    }

    @Override
    public void alterarFilme(String nomeFilme, Filme novosDados) {
        System.out.println("[Funcionário] Solicitado alteração do filme: " + nomeFilme + " -> " + novosDados.getNome());
    }

    @Override
    public String toString() {
        return super.toString() + " | Salário: R$ " + String.format("%.2f", salario) + " [Funcionário]";
    }
}
