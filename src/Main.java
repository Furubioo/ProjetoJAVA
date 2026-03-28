public class Main {
    public static void main(String[] args) {
        System.out.println("*** INICIANDO SISTEMA DE CINEMA ***\n");

        Critico c1 = new Critico("Carlos", "12345678910", "123", 25, 'm', "carlosjose@gmail.com", "Carlos j silva", "54237896341","345","Delart");
        Critico c2 = new Critico("Joana", "90123456724", "456", 32, 'f', "joanamaria@gmail.com","Joana M Almeida", "754332959994", "366", "Van Marrer");
        Critico c3 = new Critico("José", "45678923672", "678", 20, 'm', "joseangelo@gmail.com", "José A Siva", "98453267210", "742","Herbert Richers");

        Estudante e1 = new Estudante("Carol", "86687570365", "236", 22, 'f', "carolmaria@gmail.com", "Carol m Dantas","25418791237","763");
        
        
        Filme f1 = new Filme("Gato de botas 2", 100,"O gato de botas descobre que a sua paixão pela aventura cobra seu preço...",20.0);
        Filme f2 = new Filme("John Wick 4", 169, "John Wick enfrenta os seus adversários mais letais nesta próxima quarta parte da série",25.0);
        Filme f3 = new Filme("Crepúsculo",102 , "Bella Swan, uma adolescente que se muda para Forks e se apaixona por Edward Cullen.", 15.0);



        Sessao s1 = new Sessao(f1,"08:00 - 10:00");
        Sessao s2 = new Sessao(f2,"16:00 - 18:00");
        Sessao s3 = new Sessao(f3, "20:00 - 22:00");


        Sala sala1 = new Sala(6);
        sala1.adicionarSessao(s1, 0);
        sala1.adicionarSessao(s2, 4);



        Usuario u1 = new Usuario("Maria Clara", "12345678900", "123", 25, 'F', "Clara@gmail.com", "Maria C A Ribeiro", "546389764321", "710");
        u1.realizarCompra();

        int[][] cadeirasEscolhidas = new int[10][15];
        cadeirasEscolhidas[5][7] = 1;

        s1.getCadeiras()[5][7] = true;

        Bilhete bilheteMaria = new Bilhete(u1, 1, s1, f1, f1.getValor(), cadeirasEscolhidas);

        Compra compraAtual = new Compra();
        compraAtual.comprarBilhetes(bilheteMaria);
        compraAtual.adicionarProduto("Pipoca Grande",20.0);
        compraAtual.adicionarProduto("Refrigerante 500ml",15.0);

        System.out.println("\n*** RESUMO DA COMPRA ***");
        System.out.println("Cliente: "+ u1.getUser());
        System.out.println("Filme: "+ compraAtual.getBilhetes()[0].getFilme().getNome());
        System.out.println("Sessão: "+ compraAtual.getBilhetes()[0].getSessao().getHorario());
        System.out.println("Cadeira do cliente: " +bilheteMaria.LocalCadeiras(cadeirasEscolhidas));
        System.out.println("Valor do ingresso: R$ "+compraAtual.getBilhetes()[0].getValor());
        


    }
}
