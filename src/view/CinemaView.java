package view;

import controller.CinemaController;
import controller.FilmeController;
import controller.UsuarioController;
import model.Bilhete;
import model.Compra;
import model.CupomPromocional;
import model.Filme;
import model.Sala;
import model.Sessao;
import model.TipoSala;
import model.Usuario;
import model.VendasException;

import java.util.Scanner;

public class CinemaView {

    private final Scanner scanner = new Scanner(System.in);
    private final CinemaController cinemaController;
    private final FilmeController filmeController;
    private final UsuarioController usuarioController;

    public CinemaView(CinemaController cinemaController, FilmeController filmeController, UsuarioController usuarioController) {
        this.cinemaController  = cinemaController;
        this.filmeController   = filmeController;
        this.usuarioController = usuarioController;
    }

    public void iniciar() {
        System.out.println("=== Bem-vindo ao Sistema de Cinema ===");
        menuLogin();
    }

    private void menuLogin() {
        int opcao;
        do {
            System.out.println("\n--- Login ---");
            System.out.println("1. Entrar");
            System.out.println("0. Sair");
            opcao = lerInt();
            switch (opcao) {
                case 1: 
                    fazerLogin(); 
                    break;
                case 0: 
                    System.out.println("Encerrando..."); 
                    break;
                default: 
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void fazerLogin() {
        System.out.print("Usuário: ");
        String user = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario u = usuarioController.buscarPorLogin(user, senha);
        if (u == null) {
            System.out.println("Usuário ou senha incorretos.");
            return;
        }
        cinemaController.setUsuarioLogado(u);
        System.out.println("Bem-vindo, " + u.getUser() + "!");
        menuPrincipal();
    }

    private void menuPrincipal() {
        int opcao;
        do {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Ver filmes em cartaz");
            System.out.println("2. Comprar ingresso");
            System.out.println("3. Gerenciar filmes (Funcionário/Admin)");
            System.out.println("0. Sair");
            opcao = lerInt();
            switch (opcao) {
                case 1: 
                    exibirFilmes();        
                    break;
                case 2: 
                    fluxoCompra();         
                    break;
                case 3: 
                    menuGerenciarFilmes(); 
                    break;
                case 0: 
                    cinemaController.setUsuarioLogado(null); 
                    break;
                default: 
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void exibirFilmes() {
        Filme[] filmes = filmeController.getFilmes();
        int qtd = filmeController.getQtdFilmes();
        if (qtd == 0) {
            System.out.println("Nenhum filme em cartaz."); 
            return; 
        }
        System.out.println("\n--- Filmes em Cartaz ---");
        for (int i = 0; i < qtd; i++) {
            Filme f = filmes[i];
            System.out.printf("%d. %s | %dmin | R$ %.2f | Nota: %.1f%n", 
            i + 1, f.getNome(), f.getDuracao(), f.getValor(), f.getNota());
        }
    }

    private void fluxoCompra() {
        Sala sala = escolherSala();
        if (sala == null) return;

        exibirFilmes();
        System.out.print("Escolha o número do filme: ");
        int idx = lerInt() - 1;
        Filme[] filmes = filmeController.getFilmes();
        if (idx < 0 || idx >= filmeController.getQtdFilmes()) {
            System.out.println("Filme inválido.");
            return;
        }

        Sessao sessao = new Sessao(filmes[idx], "19:00");
        sala.adicionarSessao(sessao, 0);

        System.out.print("Quantos bilhetes? ");
        int qtd = lerInt();
        if (qtd <= 0) {
            System.out.println("Quantidade inválida.");
            return; 
        }

        CupomPromocional cupom = escolherCupom();

        Compra compra = new Compra();

        try {
            if (qtd == 1) {
                exibirMapa(sessao);
                System.out.print("Fileira (A-J): ");
                char fileira = scanner.nextLine().toUpperCase().charAt(0);
                System.out.print("Coluna (1-15): ");
                int coluna = lerInt() - 1;

                Bilhete bilhete = cinemaController.comprarBilhete(sessao, sala, fileira - 'A', coluna, cupom);
                compra.comprarBilhetes(bilhete, cupom);
                System.out.println("\nV Compra realizada!");
                System.out.println(bilhete);

            } 
            else {
                System.out.println("\nBuscando " + qtd + " cadeiras consecutivas...");
                exibirMapa(sessao);

                Bilhete[] bilhetes = cinemaController.comprarMultiplosBilhetes(sessao, sala, qtd, cupom);
                if (bilhetes.length == 0) {
                    System.out.println("Sem cadeiras consecutivas suficientes.");
                    return;
                }
                for (Bilhete b : bilhetes)
                    compra.comprarBilhetes(b, cupom);
                    System.out.println("\nV " + qtd + " bilhetes comprados!");
                for (Bilhete b : bilhetes) 
                    System.out.println("  " + b);
            }

            System.out.printf("Total: R$ %.2f%n", compra.calcularTotal());

        } catch (VendasException e) {
            System.out.println("\nX " + e.getMessage());
        }
    }

    private Sala escolherSala() {
        System.out.println("\n--- Escolha a Sala ---");
        TipoSala[] tipos = TipoSala.values();
        for (int i = 0; i < tipos.length; i++)
            System.out.printf("%d. %s (x%.1f)%n", i + 1, tipos[i].getTipo(), tipos[i].getMultiplicador());
        System.out.print("Escolha: ");
        int op = lerInt() - 1;
        if (op < 0 || op >= tipos.length) { 
            System.out.println("Opção inválida."); 
            return null; 
        }
        return new Sala(8, tipos[op]);
    }

    private void exibirMapa(Sessao sessao) {
        System.out.println("\n  ===== TELA =====");
        System.out.print("   ");
        for (int c = 1; c <= 15; c++) 
            System.out.printf("%2d", c);
        System.out.println();
        boolean[][] cadeiras = sessao.getCadeiras();
        for (int l = 0; l < 10; l++) {
            System.out.printf("%c  ", (char)('A' + l));
            for (int c = 0; c < 15; c++) 
                System.out.print(cadeiras[l][c] ? " X" : " O");
            System.out.println();
        }
        System.out.println("O = livre   X = ocupado\n");
    }

    private CupomPromocional escolherCupom() {
        System.out.print("Deseja usar cupom? (S/N): ");
        if (!scanner.nextLine().equalsIgnoreCase("S"))
            return CupomPromocional.NENHUM;
        System.out.println("1. DESCONTO10 (10%)  2. DESCONTO20 (20%)  3. MEIA (50%)");
        System.out.print("Escolha: ");
        switch (lerInt()) {
            case 1: 
                return CupomPromocional.DESCONTO10;
            case 2: 
                return CupomPromocional.DESCONTO20;
            case 3: 
                return CupomPromocional.MEIA;
            default: 
                return CupomPromocional.NENHUM;
        }
    }

    private void menuGerenciarFilmes() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Filmes ---");
            System.out.println("1. Incluir  2. Excluir  3. Alterar  0. Voltar");
            opcao = lerInt();
            switch (opcao) {
                case 1: 
                    incluirFilme(); 
                    break;
                case 2: 
                    excluirFilme(); 
                    break;
                case 3: 
                    alterarFilme(); 
                    break;
                case 0: 
                    break;
                default: 
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void incluirFilme() {
        System.out.print("Nome: ");         
        String nome = scanner.nextLine();
        System.out.print("Duração (min): ");
        int dur = lerInt();
        System.out.print("Sinopse: ");      
        String sinopse = scanner.nextLine();
        System.out.print("Valor (R$): ");   
        double valor = lerDouble();
        filmeController.incluirFilme(new Filme(nome, dur, sinopse, valor));
        System.out.println("Filme incluído.");
    }

    private void excluirFilme() {
        System.out.print("Nome do filme: ");
        filmeController.excluirFilme(scanner.nextLine());
    }

    private void alterarFilme() {
        System.out.print("Nome atual: ");   
        String nome = scanner.nextLine();
        System.out.print("Novo nome: ");    
        String novoNome = scanner.nextLine();
        System.out.print("Duração (min): ");
        int dur = lerInt();
        System.out.print("Sinopse: ");      
        String sinopse = scanner.nextLine();
        System.out.print("Valor (R$): ");   
        double valor = lerDouble();
        filmeController.alterarFilme(nome, new Filme(novoNome, dur, sinopse, valor));
    }

    private int lerInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim()); 
        } catch (NumberFormatException e) { 
            System.out.println("Entrada inválida, usando 0."); 
            return 0; 
        }
    }

    private double lerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim().replace(",", ".")); 
        } catch (NumberFormatException e) { 
            System.out.println("Entrada inválida, usando 0.0."); 
            return 0.0; 
        }
    }
}