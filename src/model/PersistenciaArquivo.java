package model;

import java.io.*;
import model.Critico;
import model.Estudante;

public class PersistenciaArquivo {

    private static final String ARQUIVO_USUARIOS = "usuarios.txt";
    private static final String ARQUIVO_FILMES = "filmes.txt";
    private static final int MAX = 100;

    public static void salvarFilmes(Filme[] filmes, int qtd) {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ARQUIVO_FILMES))) {
            for (int i = 0; i < qtd; i++) {
                Filme f = filmes[i];
                escritor.println(f.getNome() + "|" + f.getDuracao() + "|" +
                f.getSinopse().replace("|", " ") + "|" + f.getValor());
            }
            System.out.println("Filmes salvos: " + qtd + " registro(s).");
        } catch (IOException e) {
            System.out.println("Erro ao salvar filmes: " + e.getMessage());
        }
    }

    public static Filme[] carregarFilmes(int[] qtdLida) {
        Filme[] filmes = new Filme[MAX];
        int qtd = 0;

        try (BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_FILMES))) {
            String linha;
            while ((linha = leitor.readLine()) != null && qtd < MAX) {
                String[] c = linha.split("\\|");
                if (c.length == 4) {
                    filmes[qtd++] = new Filme(c[0], Integer.parseInt(c[1]),
                    c[2], Double.parseDouble(c[3]));
                }
            }
            System.out.println("Filmes carregados: " + qtd + " registro(s).");
        } catch (IOException e) {
            System.out.println("Arquivo de filmes não encontrado. Iniciando vazio.");
        }
        qtdLida[0] = qtd;
        return filmes;
    }

    public static void salvarUsuarios(Usuario[] usuarios, int qtd) {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ARQUIVO_USUARIOS))) {
            for (int i = 0; i < qtd; i++) {
                Usuario u = usuarios[i];
                String tipo;
                if (u instanceof Critico) {
                    tipo = "CRITICO:" + ((Critico) u).getOrigem();
                } 
                else if (u instanceof Estudante) {
                    tipo = "ESTUDANTE";
                } 
                else {
                    tipo = "USUARIO";
                }

                escritor.println(
                    u.getUser() + "|" +
                    u.getCpf() + "|" +
                    u.getSenha() + "|" +
                    u.getIdade() + "|" +
                    u.getSexo() + "|" +
                    u.getEmail() + "|" +
                    u.getNomecartao() + "|" +
                    u.getNumerocartao() + "|" +
                    u.getCodigocartao() + "|" +
                    tipo
                );
            }
            System.out.println("Usuários salvos: " + qtd + " registro(s).");
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    public static Usuario[] carregarUsuarios(int[] qtdLida) {
        Usuario[] usuarios = new Usuario[MAX];
        int qtd = 0;

        try (BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
            String linha;
            while ((linha = leitor.readLine()) != null && qtd < MAX) {
                String[] c = linha.split("\\|");
                if (c.length < 9) continue;

                String user = c[0];
                String cpf = c[1];
                String senha = c[2];
                int idade = Integer.parseInt(c[3]);
                char sexo = c[4].charAt(0);
                String email = c[5];
                String nomeCartao = c[6];
                String numCartao = c[7];
                String codCartao = c[8];
                String tipoStr = c.length >= 10 ? c[9] : "USUARIO";

                Usuario u;
                if (tipoStr.startsWith("CRITICO:")) {
                    String origem = tipoStr.substring(8);
                    u = new Critico(user, cpf, senha, idade, sexo, email, nomeCartao, numCartao, codCartao, origem);
                } 
                else if (tipoStr.equals("ESTUDANTE")) {
                    u = new Estudante(user, cpf, senha, idade, sexo, email, nomeCartao, numCartao, codCartao);
                } 
                else {
                    u = new Usuario(user, cpf, senha, idade, sexo, email, nomeCartao, numCartao, codCartao);
                }
                usuarios[qtd++] = u;
            }
            System.out.println("Usuários carregados: " + qtd + " registro(s).");
        } catch (IOException e) {
            System.out.println("Arquivo de usuários não encontrado. Iniciando vazio.");
        }

        qtdLida[0] = qtd;
        return usuarios;
    }
}