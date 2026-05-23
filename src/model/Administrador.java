package model;

public class Administrador extends Base implements GerenciaDeFilmes {

    private double salario;
    private String id;
    private Base[] usuarios;
    private int qtdUsuarios;
    private Filme[] catalogoFilmes;
    private int qtdFilmes;
    private static final int MAX = 100;

    public Administrador(String nome, int idade, String email, double salario, String id) {
        super(nome, idade, email);
        this.salario = salario;
        this.id = id;
        this.usuarios = new Base[MAX];
        this.qtdUsuarios = 0;
        this.catalogoFilmes = new Filme[MAX];
        this.qtdFilmes = 0;
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
                for (int j = i; j < qtdUsuarios - 1; j++) 
                    usuarios[j] = usuarios[j + 1];
                    usuarios[--qtdUsuarios] = null;
                    System.out.println("Usuário removido: " + emailUsuario);
                    return;
            }
        }
        System.out.println("Usuário não encontrado: " + emailUsuario);
    }

    public void listarUsuarios() {
        if (qtdUsuarios == 0) { 
            System.out.println("Nenhum usuário cadastrado."); return; 
        }
        for (int i = 0; i < qtdUsuarios; i++) 
            System.out.println("  -> " + usuarios[i]);
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

    public void listarFilmes() {
        if (qtdFilmes == 0) { 
            System.out.println("Nenhum filme cadastrado."); 
            return; 
        }
        for (int i = 0; i < qtdFilmes; i++) 
            System.out.println("  -> " + catalogoFilmes[i]);
    }

    @Override
    public String toString() {
        return super.toString() + " | Salário: R$ " + String.format("%.2f", salario) +
               " | ID: " + id + " [Administrador]";
    }
}