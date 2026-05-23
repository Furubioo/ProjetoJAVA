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
        this.filmes    = PersistenciaArquivo.carregarFilmes(qtdLida);
        this.qtdFilmes = qtdLida[0];
    }

    public void salvarFilmes() {
        PersistenciaArquivo.salvarFilmes(filmes, qtdFilmes);
    }

    public void incluirFilme(Filme filme) {
        if (qtdFilmes < MAX) {
            filmes[qtdFilmes++] = filme;
            salvarFilmes();
        } else {
            System.out.println("Catálogo cheio.");
        }
    }

    public void excluirFilme(String nome) {
        for (int i = 0; i < qtdFilmes; i++) {
            if (filmes[i].getNome().equalsIgnoreCase(nome)) {
                for (int j = i; j < qtdFilmes - 1; j++) 
                    filmes[j] = filmes[j + 1];
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