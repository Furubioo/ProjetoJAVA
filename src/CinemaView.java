package view;

import model.*;
import controller.CinemaController;
import controller.FilmeController;
import controller.UsuarioController;

import java.util.Scanner;

public class CinemaView {

    private final Scanner scanner = new Scanner(System.in);
    private final CinemaController cinemaController;
    private final FilmeController filmeController;
    private final UsuarioController usuarioController;

    public CinemaView(CinemaController cinemaController,
                      FilmeController filmeController,
                      UsuarioController usuarioController) {
        this.cinemaController = cinemaController;
        this.filmeController = filmeController;
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
                case 1: fazerLogin(); break;
                case 0: System.out.println("Encerrando..."); break;
                default: System.out.println("Opção inválida.");
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
            System.out.println("Usuário ou senha incorretos. Caso não tenha cadastro, solicite ao administrador.");
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
                case 1: exibirFilmes(); break;
                case 2: fluxoCompra(); break;
                case 3: menuGerenciarFilmes(); break;
                case 0: cinemaController.setUsuarioLogado(null); break;
                default: System.out.println("Opção inválida.");
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
            System.out.printf("%d. %s | Duração: %dmin | R$ %.2f | Nota: %.1f%n",
                    i + 1, f.getNome(), f.getDuracao(), f.getValor(), f.getNota());
        }
    }

    private void fluxoCompra() {
        exibirFilmes();
        System.out.print("Escolha o número do filme: ");
        int idx = lerInt() - 1;
        Filme[] filmes = filmeController.getFilmes();
        if (idx < 0 || idx >= filmeController.getQtdFilmes()) {
            System.out.println("Filme inválido.");
            return;
        }

        Filme filmeSelecionado = filmes[idx];
        Sessao sessao = new Sessao(filmeSelecionado, "19:00");
        Sala sala = new Sala(1);
        sala.adicionarSessao(sessao, 0);

        exibirMapa(sessao);

        System.out.print("Fileira (A-J): ");
        char fileira = scanner.nextLine().toUpperCase().charAt(0);
        System.out.print("Coluna (1-15): ");
        int coluna = lerInt() - 1;
        int linha = fileira - 'A';

        System.out.println("Deseja usar cupom? (S/N): ");
        String usaCupom = scanner.nextLine();
        CupomPromocional cupom = CupomPromocional.NENHUM;
        if (usaCupom.equalsIgnoreCase("S")) {
            cupom = escolherCupom();
        }

        try {
            Compra compra = new Compra();
            Bilhete bilhete = cinemaController.comprarBilhete(sessao, sala, linha, coluna, cupom);
            compra.comprarBilhetes(bilhete);
            System.out.println("\n✔ Compra realizada com sucesso!");
            System.out.println(bilhete);
        } catch (VendasException e) {
            System.out.println("\n✘ " + e.getMessage());
        }
    }

    private void exibirMapa(Sessao sessao) {
        System.out.println("\n  TELA  ");
        System.out.print("   ");
        for (int c = 1; c <= 15; c++) System.out.printf("%2d", c);
        System.out.println();
        boolean[][] cadeiras = sessao.getCadeiras();
        for (int l = 0; l < 10; l++) {
            System.out.printf("%c  ", (char)('A' + l));
            for (int c = 0; c < 15; c++) {
                System.out.print(cadeiras[l][c] ? " X" : " O");
            }
            System.out.println();
        }
        System.out.println("O = livre   X = ocupado");
    }

    private CupomPromocional escolherCupom() {
        System.out.println("1. DESCONTO10 (10%)");
        System.out.println("2. DESCONTO20 (20%)");
        System.out.println("3. MEIA (50%)");
        System.out.print("Escolha: ");
        switch (lerInt()) {
            case 1: return CupomPromocional.DESCONTO10;
            case 2: return CupomPromocional.DESCONTO20;
            case 3: return CupomPromocional.MEIA;
            default: return CupomPromocional.NENHUM;
        }
    }

    private void menuGerenciarFilmes() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Filmes ---");
            System.out.println("1. Incluir filme");
            System.out.println("2. Excluir filme");
            System.out.println("3. Alterar filme");
            System.out.println("0. Voltar");
            opcao = lerInt();

            switch (opcao) {
                case 1: incluirFilme(); break;
                case 2: excluirFilme(); break;
                case 3: alterarFilme(); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
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
        System.out.println("Filme incluído com sucesso.");
    }

    private void excluirFilme() {
        System.out.print("Nome do filme a excluir: ");
        filmeController.excluirFilme(scanner.nextLine());
    }

    private void alterarFilme() {
        System.out.print("Nome do filme a alterar: ");
        String nome = scanner.nextLine();
        System.out.print("Novo nome: ");
        String novoNome = scanner.nextLine();
        System.out.print("Nova duração (min): ");
        int dur = lerInt();
        System.out.print("Nova sinopse: ");
        String sinopse = scanner.nextLine();
        System.out.print("Novo valor (R$): ");
        double valor = lerDouble();
        filmeController.alterarFilme(nome, new Filme(novoNome, dur, sinopse, valor));
    }

    private int lerInt() {
        try {
            int v = Integer.parseInt(scanner.nextLine().trim());
            return v;
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