package com.cinema.backend.model;

public interface GerenciaDeFilmes {
    void incluirFilme(Filme filme);
    void excluirFilme(String tituloFilme);
    void alterarFilme(String tituloFilme, Filme novosDados);
}
