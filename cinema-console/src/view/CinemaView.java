package view;

import controller.CinemaController;
import controller.FilmeController;
import controller.UsuarioController;
import model.Administrador;
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
    private Sala[] salas;
    private boolean salasInicializadas = false;

    public CinemaView(CinemaController cinemaController, FilmeController filmeController,
            UsuarioController usuarioController) {
        this.cinemaController = cinemaController;
        this.filmeController = filmeController;
        this.usuarioController = usuarioController;
    }

    public void iniciar() {
        Terminal.limparTela();
        Terminal.titulo("Sistema de Cinema");
        menuLogin();
    }

    public void resetarSalas() {
        salasInicializadas = false;
    }

    private void inicializarSalas() {
        if (salasInicializadas)
            return;
        TipoSala[] tipos = TipoSala.values();
        salas = new Sala[tipos.length];
        Filme[] filmes = filmeController.getFilmes();
        int qtdFilmes = filmeController.getQtdFilmes();
        for (int s = 0; s < tipos.length; s++) {
            salas[s] = new Sala(tipos[s]);
            for (int i = 0; i < Sala.HORARIOS.length; i++) {
                if (qtdFilmes > 0) {
                    salas[s].adicionarSessao(
                            new Sessao(filmes[i % qtdFilmes], Sala.HORARIOS[i]), i);
                }
            }
        }
        salasInicializadas = true;
    }

    private void exibirIdentidade() {
        Usuario u = cinemaController.getUsuarioLogado();
        if (cinemaController.isAdminLogado()) {
            System.out.println(Terminal.AMARELO + Terminal.BOLD
                    + "  [Logado como: Administrador]"
                    + Terminal.RESET);
        } else if (u instanceof Critico) {
            Critico c = (Critico) u;
            System.out.println(Terminal.CIANO + Terminal.BOLD
                    + "  [Logado como: " + u.getUser()
                    + " | Critico - " + c.getOrigem() + " | entrada gratuita]"
                    + Terminal.RESET);
        } else if (u instanceof Estudante) {
            System.out.println(Terminal.VERDE + Terminal.BOLD
                    + "  [Logado como: " + u.getUser()
                    + " | Estudante - meia-entrada automatica]"
                    + Terminal.RESET);
        } else if (u != null) {
            System.out.println(Terminal.BRANCO + Terminal.BOLD
                    + "  [Logado como: " + u.getUser()
                    + " | Usuario Comum]"
                    + Terminal.RESET);
        }
    }

    private void menuLogin() {
        int opcao;
        do {
            Terminal.secao("Login");
            System.out.println("  1. Entrar");
            System.out.println("  2. Cadastrar");
            System.out.println("  0. Sair");
            Terminal.separador();
            System.out.print("  Escolha: ");
            opcao = lerInt();
            switch (opcao) {
                case 1 -> fazerLogin();
                case 2 -> cadastrarUsuario();
                case 0 -> Terminal.info("Encerrando o sistema. Ate logo!");
                default -> Terminal.aviso("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    private void fazerLogin() {
        Terminal.secao("Acesso ao Sistema");
        System.out.print("  Usuario: ");
        String user = scanner.nextLine();
        System.out.print("  Senha: ");
        String senha = scanner.nextLine();

        if ("admin".equals(user) && "admin".equals(senha)) {
            Administrador admin = usuarioController.getAdminPadrao();
            cinemaController.setAdminLogado(admin);
            cinemaController.setUsuarioLogado(null);
            Terminal.sucesso("Bem-vindo, Administrador!");
            menuPrincipal();
            cinemaController.setAdminLogado(null);
            return;
        }

        Usuario u = usuarioController.buscarPorLogin(user, senha);
        if (u == null) {
            Terminal.erro("Usuario ou senha incorretos.");
            return;
        }

        cinemaController.setUsuarioLogado(u);
        cinemaController.setAdminLogado(null);

        String tipoMsg;
        if (u instanceof Critico) {
            tipoMsg = " [Critico - " + ((Critico) u).getOrigem() + "] (entrada gratuita)";
        } else if (u instanceof Estudante) {
            tipoMsg = " [Estudante - meia-entrada automatica]";
        } else {
            tipoMsg = " [Usuario Comum]";
        }

        Terminal.sucesso("Bem-vindo, " + u.getUser() + "!" + tipoMsg);
        menuPrincipal();
        cinemaController.setUsuarioLogado(null);
    }

    private void cadastrarUsuario() {
        Terminal.secao("Cadastro de Usuario");
        System.out.print("  Usuario (login): ");
        String user = scanner.nextLine();
        System.out.print("  CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("  Senha: ");
        String senha = scanner.nextLine();
        System.out.print("  Idade: ");
        int idade = lerInt();
        System.out.print("  Sexo (M/F): ");
        String sexoStr = scanner.nextLine().toUpperCase().trim();
        char sexo = sexoStr.isEmpty() ? 'M' : sexoStr.charAt(0);
        System.out.print("  Email: ");
        String email = scanner.nextLine();
        System.out.print("  Nome no cartao: ");
        String nomeCartao = scanner.nextLine();
        System.out.print("  Numero do cartao: ");
        String numeroCartao = scanner.nextLine();
        System.out.print("  Codigo verificador: ");
        String codigoCartao = scanner.nextLine();

        Terminal.secao("Tipo de Conta");
        System.out.println("  1. Usuario comum");
        System.out.println("  2. Estudante");
        System.out.println("  3. Critico");
        System.out.print("  Escolha: ");
        int tipo = lerInt();

        Usuario novoUsuario;
        switch (tipo) {
            case 2 -> novoUsuario = new model.Estudante(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao,
                    codigoCartao);
            case 3 -> {
                System.out.print("  Origem (veiculo de critica): ");
                String origem = scanner.nextLine();
                novoUsuario = new model.Critico(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao,
                        codigoCartao, origem);
            }
            default ->
                novoUsuario = new Usuario(user, cpf, senha, idade, sexo, email, nomeCartao, numeroCartao, codigoCartao);
        }

        usuarioController.adicionarUsuario(novoUsuario);
        Terminal.sucesso("Usuario cadastrado com sucesso!");
    }

    private void menuPrincipal() {
        int opcao;
        do {
            Terminal.secao("Menu Principal");
            exibirIdentidade();
            Terminal.separador();
            System.out.println("  1. Ver filmes em cartaz");
            System.out.println("  2. Comprar ingresso");
            System.out.println("  3. Gerenciar filmes");
            System.out.println("  0. Sair");
            Terminal.separador();
            System.out.print("  Escolha: ");
            opcao = lerInt();
            switch (opcao) {
                case 1 -> exibirFilmes();
                case 2 -> {
                    if (cinemaController.getUsuarioLogado() == null) {
                        Terminal.erro("Apenas usuarios podem comprar ingressos.");
                    } else {
                        fluxoCompra();
                    }
                }
                case 3 -> menuGerenciarFilmes();
                case 0 -> {
                }
                default -> Terminal.aviso("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    private void listarFilmesParaGerencia() {
        Filme[] filmes = filmeController.getFilmes();
        int qtd = filmeController.getQtdFilmes();
        if (qtd == 0) {
            Terminal.aviso("Nenhum filme cadastrado.");
            return;
        }
        Terminal.secao("Filmes Cadastrados");
        for (int i = 0; i < qtd; i++) {
            Filme f = filmes[i];
            System.out.printf("  %d. %-30s %dmin  R$ %.2f  Nota: %.1f%n",
                    i + 1, f.getNome(), f.getDuracao(), f.getValor(), f.getNota());
            System.out.printf("     Sinopse: %s%n", f.getSinopse());
        }
        Terminal.separador();
    }

    private void exibirFilmes() {
        Filme[] filmes = filmeController.getFilmes();
        int qtd = filmeController.getQtdFilmes();
        if (qtd == 0) {
            Terminal.aviso("Nenhum filme em cartaz.");
            return;
        }
        Terminal.secao("Filmes em Cartaz");
        for (int i = 0; i < qtd; i++) {
            Filme f = filmes[i];
            System.out.printf("  %d. %-30s %dmin  R$ %5.2f  Nota: %.1f%n",
                    i + 1, f.getNome(), f.getDuracao(), f.getValor(), f.getNota());
        }
        Terminal.separador();
    }

    private void fluxoCompra() {
        exibirIdentidade();
        inicializarSalas();

        Sala sala = escolherSala();
        if (sala == null)
            return;

        Filme[] filmes = filmeController.getFilmes();
        int qtdFilmes = filmeController.getQtdFilmes();

        if (qtdFilmes == 0) {
            Terminal.aviso("Nenhum filme em cartaz.");
            return;
        }

        Terminal.secao("Filmes Disponiveis");
        for (int i = 0; i < qtdFilmes; i++) {
            System.out.printf("  %d. %-30s %dmin  R$ %.2f  Nota: %.1f%n",
                    i + 1, filmes[i].getNome(), filmes[i].getDuracao(),
                    filmes[i].getValor(), filmes[i].getNota());
        }

        Terminal.separador();
        System.out.print("  Escolha o filme: ");
        int idxFilme = lerInt() - 1;

        if (idxFilme < 0 || idxFilme >= qtdFilmes) {
            Terminal.erro("Filme invalido.");
            return;
        }

        Filme filmeSelecionado = filmes[idxFilme];

        Sessao[] sessoes = sala.getListaSessoes();
        int[] indicesValidos = new int[sessoes.length];
        int contador = 0;

        Terminal.secao("Horarios Disponiveis -- " + filmeSelecionado.getNome());
        for (int i = 0; i < sessoes.length; i++) {
            if (sessoes[i] != null
                    && sessoes[i].isEmCartaz()
                    && sessoes[i].getFilme() != null
                    && sessoes[i].getFilme().getNome().equalsIgnoreCase(filmeSelecionado.getNome())) {
                System.out.printf("  %d. %s  R$ %5.2f  [DISPONIVEL]%n",
                        contador + 1,
                        sessoes[i].getHorario(),
                        sala.calcularValorBilhete(filmeSelecionado.getValor()));
                indicesValidos[contador] = i;
                contador++;
            }
        }

        if (contador == 0) {
            Terminal.aviso("Nenhum horario disponivel para este filme nesta sala.");
            return;
        }

        Terminal.separador();
        System.out.print("  Escolha o horario: ");
        int escolha = lerInt() - 1;

        if (escolha < 0 || escolha >= contador) {
            Terminal.erro("Horario invalido.");
            return;
        }

        Sessao sessao = sessoes[indicesValidos[escolha]];

        System.out.print("  Quantos bilhetes? ");
        int qtd = lerInt();

        if (qtd <= 0) {
            Terminal.erro("Quantidade invalida.");
            return;
        }

        CupomPromocional cupom = escolherCupom();
        Compra compra = new Compra();

        try {
            if (qtd == 1) {
                exibirMapa(sessao);
                System.out.print("  Fileira (A-J): ");
                String fileiraStr = scanner.nextLine().toUpperCase().trim();

                if (fileiraStr.isEmpty()) {
                    Terminal.erro("Fileira invalida.");
                    return;
                }

                char fileira = fileiraStr.charAt(0);
                System.out.print("  Coluna (1-15): ");
                int coluna = lerInt() - 1;

                Bilhete bilhete = cinemaController.comprarBilhete(sessao, sala, fileira - 'A', coluna, cupom);
                compra.comprarBilhetes(bilhete);

                Terminal.sucesso("Compra realizada!");
                System.out.println("  " + bilhete);
            } else {
                Terminal.info("Buscando " + qtd + " cadeiras consecutivas...");
                exibirMapa(sessao);

                Bilhete[] bilhetes = cinemaController.comprarMultiplosBilhetes(sessao, sala, qtd, cupom, scanner);
                if (bilhetes.length == 0)
                    return;

                Terminal.sucesso(qtd + " bilhetes comprados!");
                for (Bilhete b : bilhetes) {
                    compra.comprarBilhetes(b);
                    System.out.println("  " + b);
                }
            }

            adicionarProdutosAoCarrinho(compra, cupom);
            Terminal.separador();
            System.out.printf(Terminal.BOLD + "  Total da compra: R$ %.2f%n" + Terminal.RESET, compra.calcularTotal());
            Terminal.separador();

        } catch (VendasException e) {
            Terminal.erro(e.getMessage());
        }
    }

    private void adicionarProdutosAoCarrinho(Compra compra, CupomPromocional cupom) {
        System.out.print("\n  Deseja adicionar produtos de balcao? (S/N): ");
        if (!scanner.nextLine().equalsIgnoreCase("S"))
            return;

        boolean continuar = true;
        while (continuar) {
            Terminal.secao("Produtos Disponiveis");
            Produto[] produtos = Produto.values();
            for (int i = 0; i < produtos.length; i++) {
                System.out.printf("  %d. %-25s R$ %.2f%n", i + 1, produtos[i].getNome(), produtos[i].getPreco());
            }
            System.out.println("  0. Finalizar");
            Terminal.separador();
            System.out.print("  Escolha: ");
            int op = lerInt();

            if (op == 0 || op < 0 || op > produtos.length) {
                continuar = false;
            } else {
                compra.adicionarProduto(produtos[op - 1], cupom);
                Terminal.sucesso("Produto adicionado!");
            }
        }
    }

    private Sala escolherSala() {
        inicializarSalas();
        Terminal.secao("Escolha a Sala");
        for (int i = 0; i < salas.length; i++)
            System.out.printf("  %d. %-10s (multiplicador x%.1f)%n",
                    i + 1, salas[i].getTipo().getTipo(), salas[i].getTipo().getMultiplicador());
        Terminal.separador();
        System.out.print("  Escolha: ");
        int op = lerInt() - 1;
        if (op < 0 || op >= salas.length) {
            Terminal.erro("Opcao invalida.");
            return null;
        }
        return salas[op];
    }

    private void exibirMapa(Sessao sessao) {
        System.out.println();
        Terminal.secao("Mapa de Assentos -- " + sessao.getFilme().getNome());
        System.out.print("      ");
        for (int c = 1; c <= 15; c++)
            System.out.printf(Terminal.DIM + "%2d " + Terminal.RESET, c);
        System.out.println();

        boolean[][] cadeiras = sessao.getCadeiras();
        for (int l = 0; l < 10; l++) {
            System.out.printf("  %c   ", (char) ('A' + l));
            for (int c = 0; c < 15; c++) {
                if (cadeiras[l][c])
                    System.out.print(Terminal.VERMELHO + " X " + Terminal.RESET);
                else
                    System.out.print(Terminal.VERDE + " O " + Terminal.RESET);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println(Terminal.VERDE + "  O = livre  " + Terminal.RESET +
                Terminal.VERMELHO + "  X = ocupado" + Terminal.RESET);
        Terminal.separador();
    }

    private CupomPromocional escolherCupom() {
        System.out.print("\n  Deseja usar cupom? (S/N): ");
        if (!scanner.nextLine().equalsIgnoreCase("S"))
            return CupomPromocional.NENHUM;

        Terminal.secao("Cupons Disponiveis");
        System.out.println("  1. DESCONTO10 -- 10% de desconto");
        System.out.println("  2. DESCONTO20 -- 20% de desconto");
        System.out.println("  3. MEIA       -- 50% de desconto");
        System.out.print("  Escolha: ");

        return switch (lerInt()) {
            case 1 -> CupomPromocional.DESCONTO10;
            case 2 -> CupomPromocional.DESCONTO20;
            case 3 -> CupomPromocional.MEIA;
            default -> CupomPromocional.NENHUM;
        };
    }

    private void menuGerenciarFilmes() {
        if (!cinemaController.isAdminLogado()) {
            Terminal.erro("Acesso negado. Apenas administradores podem gerenciar filmes.");
            return;
        }
        int opcao;
        do {
            Terminal.secao("Gerenciar Filmes");
            System.out.println("  1. Incluir filme");
            System.out.println("  2. Excluir filme");
            System.out.println("  3. Alterar filme");
            System.out.println("  0. Voltar");
            Terminal.separador();
            System.out.print("  Escolha: ");
            opcao = lerInt();
            switch (opcao) {
                case 1 -> incluirFilme();
                case 2 -> excluirFilme();
                case 3 -> alterarFilme();
                case 0 -> {
                }
                default -> Terminal.aviso("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    private void incluirFilme() {
        Terminal.secao("Incluir Filme");
        System.out.print("  Nome: ");
        String nome = scanner.nextLine();
        System.out.print("  Duracao (min): ");
        int dur = lerInt();
        System.out.print("  Sinopse: ");
        String sinopse = scanner.nextLine();
        System.out.print("  Valor (R$): ");
        double valor = lerDouble();
        filmeController.incluirFilme(new Filme(nome, dur, sinopse, valor));
        resetarSalas();
        Terminal.sucesso("Filme incluido com sucesso!");
    }

    private void excluirFilme() {
        listarFilmesParaGerencia();
        if (filmeController.getQtdFilmes() == 0)
            return;
        System.out.print("  Nome do filme a excluir: ");
        String nome = scanner.nextLine();
        filmeController.excluirFilme(nome);
        resetarSalas();
    }

    private void alterarFilme() {
        listarFilmesParaGerencia();
        if (filmeController.getQtdFilmes() == 0)
            return;
        System.out.print("  Nome atual do filme: ");
        String nome = scanner.nextLine();
        System.out.print("  Novo nome: ");
        String novoNome = scanner.nextLine();
        System.out.print("  Duracao (min): ");
        int dur = lerInt();
        System.out.print("  Sinopse: ");
        String sinopse = scanner.nextLine();
        System.out.print("  Valor (R$): ");
        double valor = lerDouble();
        filmeController.alterarFilme(nome, new Filme(novoNome, dur, sinopse, valor));
        resetarSalas();
        Terminal.sucesso("Filme alterado com sucesso!");
    }

    private int lerInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            Terminal.aviso("Entrada invalida, usando 0.");
            return 0;
        }
    }

    private double lerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            Terminal.aviso("Entrada invalida, usando 0.0.");
            return 0.0;
        }
    }
}