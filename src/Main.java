public class Main {
    public static void main(String[] args) {
        System.out.println("*** INICIANDO SISTEMA DE CINEMA ***\n");
        
        Filme f1 = new Filme("Gato de botas 2", 100,"O gato de botas descobre que a sua paixão pela aventura cobra seu preço...",20.0);
        Filme f2 = new Filme("John Wick 4", 169, "John Wick enfrenta os seus adversários mais letais nesta próxima quarta parte da série",25.0);

        Sessao s1 = new Sessao(f1,"08:00 - 10:00");
        Sessao s2 = new Sessao(f2,"16:00 - 18:00");

        Sala sala1 = new Sala(6);
        sala1.adicionarSessao(s1, 0);
        sala1.adicionarSessao(s2, 4);

        Usuario u1 = new Usuario("Maria Clara", "12345678900", "123", 25, 'F', "Clara@gmail.com", "Maria C A Ribeiro", "546389764321", "710");
        u1.realizarCompra();

        int[][] cadeirasEscolhidas = new int[10][15];
        cadeirasEscolhidas[5][7] = 1;

        s1.getCadeiras()[5][7] = true;

        Bilhete bilheteMaria = new Bilhete(u1.getUser(), u1.getCpf(), 1, s1, f1, f1.getValor(), cadeirasEscolhidas);

        Compra compraAtual = new Compra();
        compraAtual.comprarBilhetes(bilheteMaria);
        compraAtual.adicionarLanche("Pipoca Grande");
        compraAtual.adicionarLanche("Refrigerante 500ml");

        System.out.println("\n*** RESUMO DA COMPRA ***");
        System.out.println("Cliente: "+u1.getUser());
        System.out.println("Filme: "+compraAtual.getBilhetes()[0].getFilme().getNome());
        System.out.println("Sessão: "+ compraAtual.getBilhetes()[0].getSessao().getHorario());
        System.out.println("Valor do ingresso: R$ "+compraAtual.getBilhetes()[0].getValor());


    }
}
