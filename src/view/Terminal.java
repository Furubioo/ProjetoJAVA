package view;

import java.io.PrintStream;

public class Terminal {

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String PRETO = "\u001B[30m";
    public static final String VERMELHO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String ROXO = "\u001B[35m";
    public static final String CIANO = "\u001B[36m";
    public static final String BRANCO = "\u001B[37m";

    public static void configurarEncoding() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
        } catch (Exception ignored) {}
    }

    public static void sucesso(String msg) {
        System.out.println(VERDE + BOLD + "[OK] " + msg + RESET);
    }

    public static void erro(String msg) {
        System.out.println(VERMELHO + BOLD + "[ERRO] " + msg + RESET);
    }

    public static void aviso(String msg) {
        System.out.println(AMARELO + BOLD + "[AVISO] " + msg + RESET);
    }

    public static void info(String msg) {
        System.out.println(CIANO + "[INFO] " + msg + RESET);
    }

    public static void titulo(String msg) {
        int largura = 45;
        String linha = "=".repeat(largura);
        System.out.println();
        System.out.println(AZUL + BOLD + "+" + linha + "+");
        System.out.printf( AZUL + BOLD + "|  %-" + (largura - 2) + "s|%n", msg);
        System.out.println(AZUL + BOLD + "+" + linha + "+" + RESET);
    }

    public static void secao(String msg) {
        System.out.println();
        System.out.println(ROXO + BOLD + "+-- " + msg + " " + "-".repeat(Math.max(0, 40 - msg.length())) + RESET);
    }

    public static void separador() {
        System.out.println(DIM + "-".repeat(47) + RESET);
    }

    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}