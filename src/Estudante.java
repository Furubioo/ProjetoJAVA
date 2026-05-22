public class Estudante extends Usuario {

    public Estudante(String user,String cpf, String senha, int idade,char sexo,String email,String nomeCartao,String numeroCartao,String codigoCartao){
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao);
    }

    @Override
    public double calcularPrecoFinal(double precoBase){
        return precoBase / 2.0;

    }

    
}
