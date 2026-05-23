package view;

import controller.CinemaController;
import controller.FilmeController;
import controller.UsuarioController;
import model.Bilhete;
import model.Compra;
import model.Critico;
import model.CupomPromocional;
import model.Estudante;
import model.Filme;
import model.Produto;
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
        System.out.println("2. Cadastrar");
        System.out.println("0. Sair");
        opcao = lerInt();
        switch (opcao) {
            case 1: 
                fazerLogin(); 
                break;
            case 2: 
                cadastrarUsuario(); 
                break;
            case 0: 
                System.out.println("Encerrando..."); 
                break;
            default: 
                System.out.println("Opção inválida.");
        }
    } 
    while (opcao != 0);
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

    private void cadastrarUsuario() {
        System.out.println("\n--- Cadastro de Usuário ---");
        System.out.print("Usuário (login): ");
        String user = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("Idade: ");
        int idade = lerInt();
        System.out.print("Sexo (M/F): ");
        char sexo = scanner.nextLine().toUpperCase().charAt(0);
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Nome no cartão: ");
        String nomeCartao = scanner.nextLine();
        System.out.print("Número do cartão: ");
        String numeroCartao = scanner.nextLine();
        System.out.print("Código verificador: ");
        String codigoCartao = scanner.nextLine();

        System.out.println("Tipo de conta:");
        System.out.println("1. Usuário comum");
        System.out.println("2. Estudante");
        System.out.println("3. Crítico");
        int tipo = lerInt();

        Usuario novoUsuario;

        switch (tipo) {
        case 2:
            novoUsuario = new Estudante(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao);
            break;
        case 3:
            System.out.print("Origem (veículo de crítica): ");
            String origem = scanner.nextLine();
            novoUsuario = new Critico(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao, origem);
            break;
        default:
            novoUsuario = new Usuario(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao);
            break;
        }

        usuarioController.adicionarUsuario(novoUsuario);
        System.out.println("Usuário cadastrado com sucesso!");
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
        } 
        while (opcao != 0);
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
        if (sala == null) 
            return;

        Filme[] filmes = filmeController.getFilmes();
        int qtdFilmes   = filmeController.getQtdFilmes();

        if (qtdFilmes == 0) {
            System.out.println("Nenhum filme em cartaz.");
            return;
        }

        String[] horarios = {
            "08:00", "10:00", "12:00", "14:00",
            "16:00", "18:00", "20:00", "22:00"
        };

        for (int i = 0; i < horarios.length; i++) {
            Filme filmeDoHorario = filmes[i % qtdFilmes];
            sala.adicionarSessao(new Sessao(filmeDoHorario, horarios[i]), i);
        }

        System.out.println("\n--- Sessões disponíveis ---");
        Sessao[] sessoes = sala.getListaSessoes();
        for (int i = 0; i < sessoes.length; i++) {
            if (sessoes[i] != null) {
                String status = sessoes[i].horarioJaPassou() ? " [ENCERRADA]" : " [DISPONÍVEL]";
                System.out.printf("%d. %s | %s | R$ %.2f%s%n", i + 1,
                sessoes[i].getHorario(),
                sessoes[i].getFilme().getNome(),
                sala.calcularValorBilhete(sessoes[i].getFilme().getValor()), status);
            }
        }

        System.out.print("Escolha a sessão: ");
        int idxSessao = lerInt() - 1;
        if (idxSessao < 0 || idxSessao >= sessoes.length || sessoes[idxSessao] == null) {
            System.out.println("Sessão inválida.");
            return;
        }

        Sessao sessao = sessoes[idxSessao];

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
                compra.comprarBilhetes(bilhete);
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
                System.out.println("\nV " + qtd + " bilhetes comprados!");
                for (Bilhete b : bilhetes) {
                    compra.comprarBilhetes(b);
                    System.out.println("  " + b);
                }
            }

            adicionarProdutosAoCarrinho(compra, cupom);
            System.out.printf("Total: R$ %.2f%n", compra.calcularTotal());

        } catch (VendasException e) {
            System.out.println("\nX " + e.getMessage());
        }
    }

    private void adicionarProdutosAoCarrinho(Compra compra, CupomPromocional cupom) {
        System.out.print("\nDeseja adicionar produtos de balcão? (S/N): ");
        if (!scanner.nextLine().equalsIgnoreCase("S")) 
            return;

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Produtos disponíveis ---");
            Produto[] produtos = Produto.values();
            for (int i = 0; i < produtos.length; i++) {
                System.out.printf("%d. %s - R$ %.2f%n", i + 1, produtos[i].getNome(), produtos[i].getPreco());
            }
            System.out.println("0. Finalizar");
            System.out.print("Escolha: ");
            int op = lerInt() - 1;

            if (op < 0 || op >= produtos.length) {
                continuar = false;
            } 
            else {
                compra.adicionarProduto(produtos[op], cupom);
                System.out.println("Produto adicionado!");
            }
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
        return new Sala(tipos[op]);
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
        } 
        while (opcao != 0);
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
        } 
        catch (NumberFormatException e) { 
            System.out.println("Entrada inválida, usando 0."); 
            return 0; 
        }
    }

    private double lerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim().replace(",", ".")); 
        } 
        catch (NumberFormatException e) { 
            System.out.println("Entrada inválida, usando 0.0."); 
            return 0.0; 
        }
    }
}