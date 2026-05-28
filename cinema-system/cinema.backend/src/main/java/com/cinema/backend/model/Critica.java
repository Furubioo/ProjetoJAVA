package com.cinema.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "criticas")
public class Critica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String origem;
    private Long criticoId;
    private String filmeNomeSnapshot;

    @Column(length = 1000)
    private String mensagem;

    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(name = "filme_id")
    private Filme filme;

    public Critica() {
    }

    public Critica(String nome, String origem, String mensagem, Filme filme) {
        this(nome, origem, mensagem, filme, null);
    }

    public Critica(String nome, String origem, String mensagem, Filme filme, Long criticoId) {
        this.nome = nome;
        this.origem = origem;
        this.mensagem = mensagem;
        this.filme = filme;
        this.criticoId = criticoId;
        this.filmeNomeSnapshot = filme != null ? filme.getNome() : "";
    }

    public Long getId() {
        return id;
    }

    public String getNomeAutor() {
        return nome;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nome = nomeAutor;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Long getCriticoId() {
        return criticoId;
    }

    public void setCriticoId(Long criticoId) {
        this.criticoId = criticoId;
    }

    public String getFilmeNomeSnapshot() {
        return filmeNomeSnapshot;
    }

    public void setFilmeNomeSnapshot(String filmeNomeSnapshot) {
        this.filmeNomeSnapshot = filmeNomeSnapshot;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
        if (filme != null && (filmeNomeSnapshot == null || filmeNomeSnapshot.isBlank())) {
            this.filmeNomeSnapshot = filme.getNome();
        }
    }
}