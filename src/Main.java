public class Main {

    private static void imprimirRecibo(Compra compra, Bilhete bilhete, int[][] cadeiras, String perfilCliente) {
        System.out.println("\n*** RECIBO FINAL ***");
        System.out.println("\n---- Bilhete(s) Comprado(s) ----");
        System.out.println("Cliente: "+compra.getBilhetes()[0].getUser().getUser()+ "("+ perfilCliente + ")");
        System.out.println("Filme: "+compra.getBilhetes()[0].getFilme().getNome());
        System.out.println("Horário: "+compra.getBilhetes()[0].getSessao().getHorario());
        System.out.println("Assento escolhido: "+bilhete.LocalCadeiras(cadeiras));
        System.out.println("Valor do ingresso pago: R$ "+compra.getBilhetes()[0].getValor());

        System.out.println("\n Produtos adquiridos: ");
        if (compra.getQtdProdutos() == 0) {
            System.out.println("- Nenhum produto adquirido.");
        }
        else {
            for(int i = 0; i < compra.getQtdProdutos();i++){
                System.out.println("- " +compra.getProduto()[i]+" | R$ "+compra.getValorProduto()[i]);
            }
        }
        double soma = 0;
        for(int i = 0; i < compra.getQtdProdutos();i++){
            soma += compra.getValorProduto()[i];
        }
        soma += compra.getBilhetes()[0].getValor();
        System.out.println("\nValor final da compra: R$ "+soma);
        System.out.println("-----------------------------------\n");

    }

    public static void main(String[] args) {
        System.out.println("*** INICIANDO SISTEMA DE CINEMA ***\n");

        
        Filme f1 = new Filme("Gato de botas 2", 100,"O gato de botas descobre que a sua paixão pela aventura cobra seu preço...",20.0);
        Filme f2 = new Filme("John Wick 4", 169, "John Wick enfrenta os seus adversários mais letais nesta próxima quarta parte da série",25.0);
        

        Sessao s1 = new Sessao(f1,"08:00 - 10:00");
        Sessao s2 = new Sessao(f2,"16:00 - 18:00");
    

        Sala salaVip = new Sala(5);
        salaVip.adicionarSessao(s1, 0);
        salaVip.adicionarSessao(s2, 1);


        Usuario uNormal = new Usuario("Carol", "11123475678", "123", 30, 'm', "joaopedro@gmail.com", "Joao p silva", "45698254145", "587");
        Critico uCritico = new Critico("Carlos", "12345678910", "123", 25, 'm', "carlosjose@gmail.com", "Carlos j silva", "54237896341","345","Delart");
        Estudante uEstudante = new Estudante("João", "86687570365", "236", 22, 'f', "carolmaria@gmail.com", "Carol m Dantas","25418791237","763");
        

        //Usuário normal
        int[][] cadeirasCarol = new int[10][15];
        cadeirasCarol[3][5] = 1;
        s2.getCadeiras()[3][5] = true;

        double precoCarol = uNormal.calcularPrecoFinal(f2.getValor());
        Bilhete bilheteCarol = new Bilhete(uNormal, 1, s2, f2, precoCarol, cadeirasCarol);

        Compra compraCarol = new Compra();
        compraCarol.comprarBilhetes(bilheteCarol);
        compraCarol.adicionarProduto(Produto.PIPOCA_GRANDE.name(), Produto.PIPOCA_GRANDE.getPreco(), CupomPromocional.DESCONTO10);
        compraCarol.adicionarProduto(Produto.REFRIGERANTE_500ML.name(), Produto.REFRIGERANTE_500ML.getPreco());

        imprimirRecibo(compraCarol, bilheteCarol, cadeirasCarol, "Normal");


        //Usuário estudante
        int [][] cadeirasJoao = new int[10][15];
        cadeirasJoao[3][7] = 1;
        s2.getCadeiras()[3][7] = true;

        double precoIngressoJoao = uEstudante.calcularPrecoFinal(f2.getValor());
        Bilhete bilheteJoao = new Bilhete(uEstudante, 1, s2, f2, precoIngressoJoao, cadeirasJoao);

        Compra compraJoao = new Compra();
        compraJoao.comprarBilhetes(bilheteJoao);
        compraJoao.adicionarProduto(Produto.PIPOCA_GRANDE.name(),Produto.PIPOCA_GRANDE.getPreco(),CupomPromocional.DESCONTO10);
        compraJoao.adicionarProduto(Produto.REFRIGERANTE_500ML.name(),Produto.REFRIGERANTE_500ML.getPreco());

        imprimirRecibo(compraJoao, bilheteJoao, cadeirasJoao, "Estudante");


        //Usuário crítico
        int[][] cadeirasCarlos = new int[10][15];
        cadeirasCarlos[5][5] = 1;
        s1.getCadeiras()[5][5] = true;

        double precoCarlos = uCritico.calcularPrecoFinal(f1.getValor());
        Bilhete bilheteCarlos = new Bilhete(uCritico, 3, s1, f1, precoCarlos, cadeirasCarlos);

        Compra compraCarlos = new Compra();
        compraCarlos.comprarBilhetes(bilheteCarlos);

        imprimirRecibo(compraCarlos, bilheteCarlos, cadeirasCarlos, "Crítico");

    }
}
