public class Main {
    public static void main(String[] args) {
        System.out.println("*** INICIANDO SISTEMA DE CINEMA ***\n");

        
        Filme f1 = new Filme("Gato de botas 2", 100,"O gato de botas descobre que a sua paixão pela aventura cobra seu preço...",20.0);
        Filme f2 = new Filme("John Wick 4", 169, "John Wick enfrenta os seus adversários mais letais nesta próxima quarta parte da série",25.0);
        

        Sessao s1 = new Sessao(f1,"08:00 - 10:00");
        Sessao s2 = new Sessao(f2,"16:00 - 18:00");
    

        Sala salaVip = new Sala(5);
        salaVip.adicionarSessao(s1, 0);
        salaVip.adicionarSessao(s2, 1);


        Usuario uNormal = new Usuario("João", "11123475678", "123", 30, 'm', "joaopedro@gmail.com", "Joao p silva", "45698254145", "587");
        Critico uCritico = new Critico("Carlos", "12345678910", "123", 25, 'm', "carlosjose@gmail.com", "Carlos j silva", "54237896341","345","Delart");
        Estudante uEstudante = new Estudante("Carol", "86687570365", "236", 22, 'f', "carolmaria@gmail.com", "Carol m Dantas","25418791237","763");
        
        System.out.println("--- AVALIAÇÕES DA CRÍTICA --- ");
        uCritico.atribuirNota(f1,9.5);
        uCritico.atribuirCritica(f1,"Uma obra-prima absoluta do cinema moderno!");
        System.out.println("O crítico " +uCritico.getUser()+ " da origem " +uCritico.getOrigem()+ " avaliou o filme: " +f1.getNome());
        System.out.println("Nota atual do filme " +f1.getNome()+ ": "+f1.getNota()+ " (" +f1.getQuantidade_criticos()+ " crítica(s) registrada(s))");


        int [][] cadeirasJoao = new int[10][15];
        cadeirasJoao[3][5] = 1;
        s2.getCadeiras()[3][5] = true;

        double precoIngressoJoao = uEstudante.calcularPrecoFinal(f2.getValor());

        Bilhete bilheteJoao = new Bilhete(uEstudante, 1, s2, f2, precoIngressoJoao, cadeirasJoao);

        Compra compraJoao = new Compra();
        compraJoao.comprarBilhetes(bilheteJoao);


        compraJoao.adicionarProduto(Produto.PIPOCA_GRANDE.name(),Produto.PIPOCA_GRANDE.getPreco(),CupomPromocional.DESCONTO10);
        compraJoao.adicionarProduto(Produto.REFRIGERANTE_500ML.name(),Produto.REFRIGERANTE_500ML.getPreco());


        System.out.println("\n*** RECIBO FINAL ***");
        System.out.println("Cliente: "+compraJoao.getBilhetes()[0].getUser().getUser()+" (Estudante)");
        System.out.println("Filme: "+compraJoao.getBilhetes()[0].getFilme().getNome());
        System.out.println("Horário: "+compraJoao.getBilhetes()[0].getSessao().getHorario());
        System.out.println("Assento escolhido: "+bilheteJoao.LocalCadeiras(cadeirasJoao));
        System.out.println("Valor do ingresso pago: "+compraJoao.getBilhetes()[0].getValor());

        System.out.println("\n Produtos adquiridos: ");
        for(int i = 0; i < compraJoao.getQtdProdutos();i++){
            System.out.println("- " +compraJoao.getProduto()[i]+" | R$ "+compraJoao.getValorProduto()[i]);
        }

    }
}
