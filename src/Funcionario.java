import java.util.ArrayList;
import java.util.List;

public class Funcionario extends Base implements GerenciaDeFilmes {

    private double salario;
    private List<Filme> catalogoFilmes;

    public Funcionario(String nome, int idade, String email, double salario) {
        super(nome, idade, email);
        this.salario = salario;
        this.catalogoFilmes = new ArrayList<>();
    }

    
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    

    @Override
    public void adicionarUsuario() {
        System.out.println(" Funcionário adicionado: " + toString());
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
        System.out.println("  Dados do funcionário atualizados: " + toString());
    }

    

    @Override
    public void incluirFilme(Filme filme) {
        catalogoFilmes.add(filme);
        System.out.println(" Filme incluído: " + filme.getTitulo());
    }

    @Override
    public void excluirFilme(String tituloFilme) {
        boolean removido = catalogoFilmes.removeIf(f -> f.getTitulo().equalsIgnoreCase(tituloFilme));
        if (removido) {
            System.out.println(" Filme removido: " + tituloFilme);
        } else {
            System.out.println("  Filme não encontrado: " + tituloFilme);
        }
    }

    @Override
    public void alterarFilme(String tituloFilme, Filme novosDados) {
        for (int i = 0; i < catalogoFilmes.size(); i++) {
            if (catalogoFilmes.get(i).getTitulo().equalsIgnoreCase(tituloFilme)) {
                catalogoFilmes.set(i, novosDados);
                System.out.println(" Filme alterado: " + tituloFilme + " → " + novosDados.getTitulo());
                return;
            }
        }
        System.out.println("  Filme não encontrado para alterar: " + tituloFilme);
    }

    public List<Filme> getCatalogoFilmes() { return catalogoFilmes; }

    @Override
    public String toString() {
        return super.toString() + " | Salário: R$ " + String.format("%.2f", salario) + " [Funcionário]";
    }
}
