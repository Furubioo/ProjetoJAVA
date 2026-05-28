package model;

public class Administrador extends Funcionario {

    private String id;

    public Administrador(String nome, int idade, String email, double salario, String id) {
        super(nome, idade, email, salario);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void adicionarUsuario(Base usuario) {
        if (usuario != null) {
            System.out.println("[Admin] Usuário adicionado: " + usuario.getNome()
                    + " | Email: " + usuario.getEmail());
        }
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
        System.out.println("[Admin] Dados do admin atualizados.");
    }

    public void excluirUsuario(Usuario[] usuarios, int[] qtd, String email) {
        for (int i = 0; i < qtd[0]; i++) {
            if (usuarios[i].getEmail().equalsIgnoreCase(email)) {
                for (int j = i; j < qtd[0] - 1; j++) {
                    usuarios[j] = usuarios[j + 1];
                }
                usuarios[--qtd[0]] = null;
                System.out.println("[Admin] Usuario removido: " + email);
                return;
            }
        }
        System.out.println("[Admin] Usuario nao encontrado: " + email);
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
        return super.toString().replace(" [Funcionário]", "")
                + " | ID: " + id + " [Administrador]";
    }
}