package model;
import model.PersistenciaArquivo;
import model.Usuario;

public class Administrador extends Base implements GerenciaDeFilmes {

    private double salario;
    private String id;
    private Base[] usuarios;
    private int qtdUsuarios;
    private static final int MAX = 100;

    public Administrador(String nome, int idade, String email, double salario, String id) {
        super(nome, idade, email);
        this.salario     = salario;
        this.id          = id;
        this.usuarios    = new Base[MAX];
        this.qtdUsuarios = 0;
    }

    public double getSalario() {
        return salario; 
    }
    public void setSalario(double salario) { 
        this.salario = salario; 
    }
    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }

    @Override
    public void adicionarUsuario(Base usuario) {
    if (qtdUsuarios < MAX) {
        usuarios[qtdUsuarios++] = usuario;
        System.out.println("Usuário adicionado pelo Administrador: " + usuario.getNome());
    } 
    else {
        System.out.println("Limite de usuários atingido.");
    }
}

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
        System.out.println("Administrador atualizado: " + toString());
    }

    public void salvarUsuario(Base usuario) {
        if (qtdUsuarios < MAX) {
            usuarios[qtdUsuarios++] = usuario;
            System.out.println("Usuário salvo: " + usuario.getNome());
        }
    }

    public void excluirUsuario(String emailUsuario) {
        for (int i = 0; i < qtdUsuarios; i++) {
            if (usuarios[i].getEmail().equalsIgnoreCase(emailUsuario)) {
                for (int j = i; j < qtdUsuarios - 1; j++) {
                    usuarios[j] = usuarios[j + 1];
                }
                usuarios[--qtdUsuarios] = null;
                System.out.println("Usuário removido: " + emailUsuario);
                return;
            }
        }
        System.out.println("Usuário não encontrado: " + emailUsuario);
    }

    public void listarUsuarios() {
        if (qtdUsuarios == 0) { 
            System.out.println("Nenhum usuário cadastrado."); 
            return; 
        }
        for (int i = 0; i < qtdUsuarios; i++)
            System.out.println("  -> " + usuarios[i]);
    }

    @Override
    public void incluirFilme(Filme filme) {
        System.out.println("[Admin] Autorizado inclusão do filme: " + filme.getNome());
    }

    @Override
    public void excluirFilme(String nomeFilme) {
        System.out.println("[Admin] Autorizado exclusão do filme: " + nomeFilme);
    }

    @Override
    public void alterarFilme(String nomeFilme, Filme novosDados) {
        System.out.println("[Admin] Autorizado alteração do filme: " + nomeFilme + " -> " + novosDados.getNome());
    }

    public void persistirUsuarios(Usuario[] usuarios, int qtd) {
        PersistenciaArquivo.salvarUsuarios(usuarios, qtd);
        System.out.println("[Administrador] Persistência de usuários realizada.");
    }

    public void persistirFilmes(Filme[] filmes, int qtd) {
        PersistenciaArquivo.salvarFilmes(filmes, qtd);
        System.out.println("[Administrador] Persistência de filmes realizada.");
    }

    @Override
    public String toString() {
        return super.toString() + " | Salário: R$ " + String.format("%.2f", salario) +
        " | ID: " + id + " [Administrador]";
    }
}
