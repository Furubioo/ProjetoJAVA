package com.cinema.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario implements GerenciaDeFilmes {

    private double salario;
    private String adminId;

    public Administrador() {
        super();
    }

    public Administrador(String user, String cpf, String senha, int idade, char sexo,
                         String email, String nomeCartao, String numeroCartao,
                         String codigoCartao, double salario, String adminId) {
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao);
        this.salario = salario;
        this.adminId = adminId;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    @Override
    public void adicionarUsuario(Base usuario) {
        if (usuario != null) {
            System.out.println("[Admin] Usuario adicionado: " + usuario.getNome()
                    + " | Email: " + usuario.getEmail());
        }
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
        System.out.println("[Admin] Dados atualizados: " + toString());
    }

    @Override
    public void incluirFilme(Filme filme) {
        System.out.println("[Admin] Filme incluido: " + filme.getNome());
    }

    @Override
    public void excluirFilme(String tituloFilme) {
        System.out.println("[Admin] Filme excluido: " + tituloFilme);
    }

    @Override
    public void alterarFilme(String tituloFilme, Filme novosDados) {
        System.out.println("[Admin] Filme alterado: " + tituloFilme + " -> " + novosDados.getNome());
    }

    public void excluirUsuario(String emailAlvo) {
        System.out.println("[Admin] Solicitada exclusao do usuario: " + emailAlvo);
    }

    @Override
    public String toString() {
        return getUser() + " | " + getEmail()
                + " | Idade: " + getIdade()
                + " | Salario: R$ " + String.format("%.2f", salario)
                + " | Admin ID: " + adminId
                + " [Administrador]";
    }
}