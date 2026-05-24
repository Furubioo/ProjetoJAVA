package model;

public class Administrador extends Base implements GerenciaDeFilmes {

    private double salario;
    private String id;

    public Administrador(String nome, int idade, String email, double salario, String id) {
        super(nome, idade, email);
        this.salario = salario;
        this.id = id;
    }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public void adicionarUsuario(Base usuario) {
        System.out.println("[Admin] Usuario adicionado: " + usuario.getNome());
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
    }

    public void excluirUsuario(String email) {
        System.out.println("[Admin] Usuario com email '" + email + "' excluido.");
    }

    @Override
    public void incluirFilme(Filme filme) {
        System.out.println("[Admin] Filme incluido: " + filme.getNome());
    }

    @Override
    public void excluirFilme(String nomeFilme) {
        System.out.println("[Admin] Filme excluido: " + nomeFilme);
    }

    @Override
    public void alterarFilme(String nomeFilme, Filme novosDados) {
        System.out.println("[Admin] Filme alterado: " + nomeFilme + " -> " + novosDados.getNome());
    }

    public void persistirFilmes(Filme[] filmes, int qtd) {
        PersistenciaArquivo.salvarFilmes(filmes, qtd);
    }

    public void persistirUsuarios(Usuario[] usuarios, int qtd) {
        PersistenciaArquivo.salvarUsuarios(usuarios, qtd);
    }

    @Override
    public String toString() {
        return super.toString() + " | Salario: R$ " + String.format("%.2f", salario)
                + " | ID: " + id + " [Administrador]";
    }
}