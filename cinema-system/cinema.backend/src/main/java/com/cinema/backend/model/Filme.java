package com.cinema.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "filmes")
public class Filme {

    public static final int MAX_CRITICAS = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int duracao;

    @Column(length = 1000)
    private String sinopse;

    private double valor;
    private double nota;
    private int quantidadeCriticos;

    @Column(length = 1000)
    private String imagemUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "filme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Critica> criticas = new ArrayList<>();

    public Filme() {
    }

    public Filme(String nome, int duracao, String sinopse, double valor) {
        this.nome = nome;
        this.duracao = duracao;
        this.sinopse = sinopse;
        this.valor = valor;
        this.nota = 0.0;
        this.quantidadeCriticos = 0;
        this.criticas = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public int getQuantidadeCriticos() {
        return quantidadeCriticos;
    }

    public void setQuantidadeCriticos(int quantidadeCriticos) {
        this.quantidadeCriticos = quantidadeCriticos;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public List<Critica> getCriticas() {
        return criticas;
    }

    public void setCriticas(List<Critica> criticas) {
        this.criticas = criticas == null ? new ArrayList<>() : criticas;
    }

    public void adicionarCritica(Critica critica) {
        if (criticas == null) {
            criticas = new ArrayList<>();
        }

        if (criticas.size() >= MAX_CRITICAS) {
            throw new IllegalStateException("Limite de criticas atingido para este filme.");
        }

        critica.setFilme(this);
        criticas.add(critica);
    }

    @Override
    public String toString() {
        return nome + " (" + duracao + " min) | R$ "
                + String.format("%.2f", valor)
                + " | Nota: " + String.format("%.1f", nota);
    }
}