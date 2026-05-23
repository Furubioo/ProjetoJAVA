package model;

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

    public Usuario(String user, String cpf, String senha, int idade, char sexo, String email, String nomeCartao, String numeroCartao, String codigoCartao) {
        this.user = user;
        this.cpf = cpf;
        this.senha = senha;
        this.idade = idade;
        this.sexo = sexo;
        this.email = email;
        this.nomeCartao = nomeCartao;
        this.numeroCartao = numeroCartao;
        this.codigoCartao = codigoCartao;
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
        System.out.println(user + " solicitou alteração da compra.");
    }
    
    public void cancelarCompra(){
        System.out.println(user + " solicitou cancelamento da compra."); 
    }

    public double calcularPrecoFinal(double precoBase) {
        return precoBase; 
    }

    @Override
    public String toString() {
        return "Usuário: " + user + " | CPF: " + cpf + " | Email: " + email;
    }
}