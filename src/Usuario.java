public class Usuario {
    private String user;
    private int cpf;
    private String senha;
    private int idade;
    private char sexo;
    private String email;
    private String nomeCartao;
    private int numeroCartao;
    private int codigoCartao;

    public Usuario(String user, int cpf, String senha, int idade, char sexo, String email, String nomeCartao, int numeroCartao, int codigoCartao){
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

    public String getUser(){
        return user;
    }
    
    public int getCpf(){
        return cpf;
    }

    public String getSenha(){
        return senha;
    }

    public int getIdade(){
        return idade;
    }

    public char getSexo(){
        return sexo;
    }

    public String getEmail(){
        return email;
    }

    public String getNomecartao(){
        return nomeCartao;
    }

    public int getNumerocartao(){
        return numeroCartao;
    }

    public int getCodigocartao(){
        return codigoCartao;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void setCpf(int cpf){
        this.cpf = cpf;
    }

    public void setSenha(String senha){
        this.senha = senha;
    }

    public void setIdade(int idade){
        this.idade = idade;
    }

    public void setSexo(char sexo){
        this.sexo = sexo;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setNomecartao(String nomeCartao){
        this.nomeCartao = nomeCartao;
    }

    public void setNumerocartao(int numeroCartao){
        this.numeroCartao = numeroCartao;
    }

    public void setCodigocartao(int codigoCartao){
        this.codigoCartao = codigoCartao;
    }
    
}
