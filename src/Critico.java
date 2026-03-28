public class Critico extends Usuario{
    private String origem;

    public Critico(String user, String cpf, String senha, int idade, char sexo, String email, String nomeCartao, String numeroCartao, String codigoCartao, String origem) {
        super(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao);
        this.origem = origem;
    }

    @Override // Para evitar a criação de um método novo em vez de sobrescrever o antigo
    public double calcularPrecoFinal(double precoBase){
        precoBase = 0;
        return precoBase;
    }


}
