import java.util.ArrayList;
import java.util.List;

public class Administrador extends Base implements GerenciaDeFilmes {

    private double salario;
    private String id;
    private List<Base> usuarios;
    private List<Filme> catalogoFilmes;

    public Administrador(String nome, int idade, String email, double salario, String id) {
        super(nome, idade, email);
        this.salario = salario;
        this.id = id;
        this.usuarios = new ArrayList<>();
        this.catalogoFilmes = new ArrayList<>();
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
    public void adicionarUsuario() {
        System.out.println("Administrador adicionado: " + toString());
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
        System.out.println("Dados do administrador atualizados: " + toString());
    }

    public void excluirUsuario(String emailUsuario) {
        boolean removido = usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(emailUsuario));
        if (removido) {
            System.out.println("Usuário removido: " + emailUsuario);
        } else {
            System.out.println("Usuário não encontrado: " + emailUsuario);
        }
    }

    public void salvarUsuario(Base usuario) {
        usuarios.add(usuario);
        System.out.println("Usuário salvo: " + usuario.getNome());
    }

    public void listarUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        System.out.println("Lista de usuários:");
        for (Base u : usuarios) {
            System.out.println("   -> " + u);
        }
    }

    public void listarFilmes() {
        if (catalogoFilmes.isEmpty()) {
            System.out.println("Nenhum filme cadastrado.");
            return;
        }
        System.out.println("Catálogo de filmes:");
        for (Filme f : catalogoFilmes) {
            System.out.println("   -> " + f);
        }
    }

    @Override
    public void incluirFilme(Filme filme) {
        catalogoFilmes.add(filme);
        System.out.println("Filme incluído: " + filme.getNome());
    }

    @Override
    public void excluirFilme(String nomeFilme) {
        boolean removido = catalogoFilmes.removeIf(f -> f.getNome().equalsIgnoreCase(nomeFilme));
        if (removido) {
            System.out.println("Filme removido: " + nomeFilme);
        } else {
            System.out.println("Filme não encontrado: " + nomeFilme);
        }
    }

    @Override
    public void alterarFilme(String nomeFilme, Filme novosDados) {
        for (int i = 0; i < catalogoFilmes.size(); i++) {
            if (catalogoFilmes.get(i).getNome().equalsIgnoreCase(nomeFilme)) {
                catalogoFilmes.set(i, novosDados);
                System.out.println("Filme alterado: " + nomeFilme + " → " + novosDados.getNome());
                return;
            }
        }
        System.out.println("Filme não encontrado para alterar: " + nomeFilme);
    }

    @Override
    public String toString() {
        return super.toString() + " | Salário: R$ " + String.format("%.2f", salario) +
               " | ID: " + id + " [Administrador]";
    }
}