package com.cinema.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoSala tipo;

    @JsonIgnore
    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL)
    private List<Sessao> sessoes = new ArrayList<>();

    public Sala() {}

    public Sala(String nome, TipoSala tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    public Long getId()                  { return id; }
    public String getNome()              { return nome; }
    public void setNome(String nome)     { this.nome = nome; }
    public TipoSala getTipo()            { return tipo; }
    public void setTipo(TipoSala tipo)   { this.tipo = tipo; }
    public List<Sessao> getSessoes()     { return sessoes; }
}