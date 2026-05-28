package com.cinema.backend.model;

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

    public double getSalario()             { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    // ── Base ───────────────────────────────────────────────────────────────

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

    // ── GerenciaDeFilmes ──────────────────────────────────────────────────

    @Override
    public void incluirFilme(Filme filme) {
        catalogoFilmes.add(filme);
        System.out.println("[Funcionário] Filme incluído: " + filme.getNome());
    }

    @Override
    public void excluirFilme(String tituloFilme) {
        boolean removido = catalogoFilmes.removeIf(f -> f.getNome().equalsIgnoreCase(tituloFilme));
        System.out.println(removido
            ? "[Funcionário] Filme removido: " + tituloFilme
            : "[Funcionário] Filme não encontrado: " + tituloFilme);
    }

    @Override
    public void alterarFilme(String tituloFilme, Filme novosDados) {
        for (int i = 0; i < catalogoFilmes.size(); i++) {
            if (catalogoFilmes.get(i).getNome().equalsIgnoreCase(tituloFilme)) {
                catalogoFilmes.set(i, novosDados);
                System.out.println("[Funcionário] Filme alterado: " + tituloFilme
                    + " -> " + novosDados.getNome());
                return;
            }
        }
        System.out.println("[Funcionário] Filme não encontrado para alterar: " + tituloFilme);
    }

    public List<Filme> getCatalogoFilmes() { return catalogoFilmes; }

    @Override
    public String toString() {
        return super.toString() + " | Salário: R$ "
            + String.format("%.2f", salario) + " [Funcionário]";
    }
}