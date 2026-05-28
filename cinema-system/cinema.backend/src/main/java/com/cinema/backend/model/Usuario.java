package com.cinema.backend.model;

import com.cinema.backend.model.strategy.EstrategiaPreco;
import com.cinema.backend.model.strategy.PrecoComum;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("COMUM")
public class Usuario extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String user;

    @Column(unique = true)
    private String cpf;

    private String senha;
    private char sexo;
    private String nomeCartao;
    private String numeroCartao;
    private String codigoCartao;

    @Transient
    private EstrategiaPreco estrategiaPreco;

    @Column(name = "tipo", insertable = false, updatable = false)
    private String dtype;

    public Usuario() {
        super();
        this.estrategiaPreco = new PrecoComum();
    }

    public Usuario(String user, String cpf, String senha, int idade, char sexo,
            String email, String nomeCartao, String numeroCartao, String codigoCartao) {
        this(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao, new PrecoComum());
    }

    public Usuario(String user, String cpf, String senha, int idade, char sexo,
            String email, String nomeCartao, String numeroCartao,
            String codigoCartao, EstrategiaPreco estrategiaPreco) {
        super(user, idade, email);
        this.user = user;
        this.cpf = cpf;
        this.senha = senha;
        this.sexo = sexo;
        this.nomeCartao = nomeCartao;
        this.numeroCartao = numeroCartao;
        this.codigoCartao = codigoCartao;
        this.estrategiaPreco = estrategiaPreco;
    }

    public Long getId() {
        return id;
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

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
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

    public String getDtype() {
        return dtype;
    }

    public double calcularPrecoFinal(double precoBase) {
        if (estrategiaPreco == null) {
            estrategiaPreco = new PrecoComum();
        }
        return estrategiaPreco.calcularPreco(precoBase);
    }

    public void setEstrategiaPreco(EstrategiaPreco estrategiaPreco) {
        this.estrategiaPreco = estrategiaPreco;
    }

    @Override
    public void adicionarUsuario(Base usuario) {
        if (usuario != null) {
            System.out.println("[Usuario] Solicitacao de adicao registrada: " + usuario.getNome());
        }
    }

    @Override
    public void alterarUsuario(String novoNome, int novaIdade, String novoEmail) {
        setNome(novoNome);
        setIdade(novaIdade);
        setEmail(novoEmail);
    }

    @Override
    public String toString() {
        return user + " | " + getEmail() + " | Idade: " + getIdade();
    }

    public void realizarCompra() {
        System.out.println(getUser() + " realizou a compra.");
    }

    public void alterarCompra() {
        System.out.println(getUser() + " solicitou alteracao da compra.");
    }

    public void cancelarCompra() {
        System.out.println(getUser() + " solicitou cancelamento da compra.");
    }

    public double comprarBilhete(double valorBase) {
        return calcularPrecoFinal(valorBase);
    }

    protected Bilhete criarBilhete(Sala sala, Sessao sessao, int linha, int coluna, double valorFinal) {
        return new Bilhete(this, sala, sessao, sessao.getFilme(), valorFinal,
                sessao.formatarCadeira(linha, coluna));
    }
}