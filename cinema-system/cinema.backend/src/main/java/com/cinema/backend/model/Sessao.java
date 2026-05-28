package com.cinema.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sessoes")
public class Sessao {

    public static final int TOTAL_LINHAS = 10;
    public static final int TOTAL_COLUNAS = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filme_id")
    private Filme filme;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    private Sala sala;

    private String horario;

    @Column(length = 1200)
    private String cadeirasOcupadas = "";

    private boolean emCartaz = true;

    public Sessao() {
    }

    public Sessao(Filme filme, Sala sala, String horario) {
        this.filme = filme;
        this.sala = sala;
        this.horario = horario;
        this.cadeirasOcupadas = "";
        this.emCartaz = true;
    }

    public Long getId() { return id; }

    public Filme getFilme() { return filme; }
    public void setFilme(Filme filme) { this.filme = filme; }

    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getCadeirasOcupadas() { return cadeirasOcupadas; }
    public void setCadeirasOcupadas(String cadeirasOcupadas) {
        this.cadeirasOcupadas = cadeirasOcupadas == null ? "" : cadeirasOcupadas;
    }

    public boolean isEmCartaz() { return emCartaz; }
    public void setEmCartaz(boolean emCartaz) { this.emCartaz = emCartaz; }

    public boolean[][] getCadeiras() {
        boolean[][] matriz = new boolean[TOTAL_LINHAS][TOTAL_COLUNAS];

        if (cadeirasOcupadas == null || cadeirasOcupadas.isBlank()) {
            return matriz;
        }

        for (String posicao : cadeirasOcupadas.split(",")) {
            String[] partes = posicao.split(":");

            if (partes.length != 2) continue;

            try {
                int linha = Integer.parseInt(partes[0]);
                int coluna = Integer.parseInt(partes[1]);

                if (posicaoValida(linha, coluna)) {
                    matriz[linha][coluna] = true;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return matriz;
    }

    public boolean posicaoValida(int linha, int coluna) {
        return linha >= 0 && linha < TOTAL_LINHAS
            && coluna >= 0 && coluna < TOTAL_COLUNAS;
    }

    public boolean cadeiraDisponivel(int linha, int coluna) {
        if (!posicaoValida(linha, coluna)) return false;

        String posicao = linha + ":" + coluna;

        if (cadeirasOcupadas == null || cadeirasOcupadas.isBlank()) {
            return true;
        }

        for (String ocupada : cadeirasOcupadas.split(",")) {
            if (ocupada.equals(posicao)) {
                return false;
            }
        }

        return true;
    }

    public boolean ocuparCadeira(int linha, int coluna) {
        if (!cadeiraDisponivel(linha, coluna)) {
            return false;
        }

        String posicao = linha + ":" + coluna;

        cadeirasOcupadas = cadeirasOcupadas == null || cadeirasOcupadas.isBlank()
            ? posicao
            : cadeirasOcupadas + "," + posicao;

        return true;
    }

    public boolean horarioJaPassou() {
        return false;
    }

    public String formatarCadeira(int linha, int coluna) {
        return String.valueOf((char) ('A' + linha)) + (coluna + 1);
    }

    @Override
    public String toString() {
        return (filme != null ? filme.getNome() : "sem filme") + " | " + horario;
    }
}