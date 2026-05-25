package controller;

import model.Filme;
import model.PersistenciaArquivo;

public class FilmeController {

    private Filme[] filmes;
    private int qtdFilmes;
    private static final int MAX = 100;

    public FilmeController() {
        this.filmes    = new Filme[MAX];
        this.qtdFilmes = 0;
    }

    public void carregarFilmes() {
        int[] qtdLida = new int[1];
        Filme[] carregados = PersistenciaArquivo.carregarFilmes(qtdLida);
        if (qtdLida[0] > 0) {
            this.filmes    = carregados;
            this.qtdFilmes = qtdLida[0];
        } else {
            carregarFilmesPadrao(); 
        }
    }

    private void carregarFilmesPadrao() {
        filmes[qtdFilmes++] = new Filme("Gato de Botas 2",           102, "As aventuras do Gato de Botas continuam.",          20.00);
        filmes[qtdFilmes++] = new Filme("Avatar: O Caminho da Agua", 192, "Jake Sully vive com sua nova familia formada.",      22.00);
        filmes[qtdFilmes++] = new Filme("John Wick 4",               169, "John Wick enfrenta seus inimigos mais poderosos.",   21.00);
        filmes[qtdFilmes++] = new Filme("Creed III",                 116, "Adonis Creed enfrenta um rival do passado.",         19.00);
        filmes[qtdFilmes++] = new Filme("Panico VI",                 123, "O retorno do Ghostface em Nova York.",               18.00);
        PersistenciaArquivo.salvarFilmes(filmes, qtdFilmes); // já persiste pra próxima execução
    }

    public void salvarFilmes() {
        PersistenciaArquivo.salvarFilmes(filmes, qtdFilmes);
    }

    public void incluirFilme(Filme filme) {
        if (qtdFilmes < MAX) {
            filmes[qtdFilmes++] = filme;
            salvarFilmes();
        } 
        else {
            System.out.println("Catálogo cheio.");
        }
    }

    public void excluirFilme(String nome) {
        for (int i = 0; i < qtdFilmes; i++) {
            if (filmes[i].getNome().equalsIgnoreCase(nome)) {
                for (int j = i; j < qtdFilmes - 1; j++) {
                    filmes[j] = filmes[j + 1];
                }
                filmes[--qtdFilmes] = null;
                salvarFilmes();
                System.out.println("Filme removido: " + nome);
                return;
            }
        }
        System.out.println("Filme não encontrado: " + nome);
    }

    public void alterarFilme(String nome, Filme novosDados) {
        for (int i = 0; i < qtdFilmes; i++) {
            if (filmes[i].getNome().equalsIgnoreCase(nome)) {
                filmes[i] = novosDados;
                salvarFilmes();
                System.out.println("Filme alterado: " + nome);
                return;
            }
        }
        System.out.println("Filme não encontrado: " + nome);
    }

    public Filme[] getFilmes() { 
        return filmes; 
    }
    public int getQtdFilmes() { 
        return qtdFilmes; 
    }

    public Filme buscarFilme(String nome) {
        for (int i = 0; i < qtdFilmes; i++)
            if (filmes[i].getNome().equalsIgnoreCase(nome))
                return filmes[i];
        return null;
    }
}
