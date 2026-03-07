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

    public Usuario(String user, String cpf, String senha, int idade, char sexo, String email, String nomeCartao, String numeroCartao, String codigoCartao){
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
    
    public String getCpf(){
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

    public String getNumerocartao(){
        return numeroCartao;
    }

    public String getCodigocartao(){
        return codigoCartao;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void setCpf(String cpf){
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

    public void setNumerocartao(String numeroCartao){
        this.numeroCartao = numeroCartao;
    }

    public void setCodigocartao(String codigoCartao){
        this.codigoCartao = codigoCartao;
    }

    public void realizarCompra(){
        System.out.println("O "+getUser()+" iniciou a compra.");
    }

    public void alterarCompra(){
        System.out.println("O "+getUser()+" solicitou a alteração da compra.");
    }

    public void cancelarCompra(){
        System.out.println("O "+getUser()+" solicitou o cancelamento da compra.");
    }
    
}
