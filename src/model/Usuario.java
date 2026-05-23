package model;

import model.strategy.EstrategiaPreco;
import model.strategy.PrecoComum;

public class Usuario {
    private String user;
    private String cpf;
    private String senha;
    private int idade;
    private char sexo;
    private String email;
    private String nomeCartao;
    private String numeroCartao;
    private String codigoCartao;
    private EstrategiaPreco estrategiaPreco;

    public Usuario(String user, String cpf, String senha, int idade, char sexo, String email,
        String nomeCartao, String numeroCartao, String codigoCartao) {
        this(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao, new PrecoComum());
    }

    public Usuario(String user, String cpf, String senha, int idade, char sexo, String email, 
        String nomeCartao, String numeroCartao, String codigoCartao,
        EstrategiaPreco estrategiaPreco) {
        this.user = user;
        this.cpf = cpf;
        this.senha = senha;
        this.idade = idade;
        this.sexo = sexo;
        this.email = email;
        this.nomeCartao = nomeCartao;
        this.numeroCartao = numeroCartao;
        this.codigoCartao = codigoCartao;
        this.estrategiaPreco = estrategiaPreco;
    }

    public EstrategiaPreco getEstrategiaPreco() {
        return estrategiaPreco;
    }

    public void setEstrategiaPreco(EstrategiaPreco estrategiaPreco) {
        this.estrategiaPreco = estrategiaPreco;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomecartao() {
        return nomeCartao;
    }

    public void setNomecartao(String nomeCartao) {
        this.nomeCartao = nomeCartao;
    }

    public String getNumerocartao() {
        return numeroCartao;
    }

    public void setNumerocartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getCodigocartao() {
        return codigoCartao;
    }

    public void setCodigocartao(String codigoCartao) {
        this.codigoCartao = codigoCartao;
    }

    public void realizarCompra() {
        System.out.println(user + " realizou a compra.");
    }

    public void alterarCompra() {
        System.out.println(user + " solicitou alteracao da compra.");
    }

    public void cancelarCompra() {
        System.out.println(user + " solicitou cancelamento da compra.");
    }

    public double calcularPrecoFinal(double precoBase) {
        return estrategiaPreco.calcularPreco(precoBase);
    }

    public Bilhete comprarBilhete(Sala sala, Sessao sessao, int linha, int coluna) {
        double valorBase = sala.calcularValorBilhete(sessao.getFilme().getValor());
        double valorFinal = calcularPrecoFinal(valorBase);
        return criarBilhete(sala, sessao, linha, coluna, valorFinal);
    }

    protected Bilhete criarBilhete(Sala sala, Sessao sessao, int linha, int coluna, double valorFinal) {
        return new Bilhete(this, sala, sessao, sessao.getFilme(), valorFinal, sessao.formatarCadeira(linha, coluna));
    }

    @Override
    public String toString() {
        return "Usuario: " + user + " | CPF: " + cpf + " | Email: " + email;
    }
}