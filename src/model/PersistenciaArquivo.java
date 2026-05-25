package model;

import java.io.*;

public class PersistenciaArquivo {

    private static final String ARQUIVO_USUARIOS = "usuarios.txt";
    private static final String ARQUIVO_FILMES   = "filmes.txt";
    private static final int    MAX              = 100;

    public static void salvarFilmes(Filme[] filmes, int qtd) {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ARQUIVO_FILMES))) {
            for (int i = 0; i < qtd; i++) {
                Filme f = filmes[i];
                // CORREÇÃO: incluir nota e quantidade_criticos na persistência
                escritor.println(
                    f.getNome()                           + "|" +
                    f.getDuracao()                        + "|" +
                    f.getSinopse().replace("|", " ")      + "|" +
                    f.getValor()                          + "|" +
                    f.getNota()                           + "|" +
                    f.getQuantidade_criticos()
                );
            }
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
                // CORREÇÃO: aceitar tanto o formato antigo (4 campos) quanto
                // o novo (6 campos com nota e quantidade_criticos)
                if (c.length >= 4) {
                    Filme f = new Filme(c[0], Integer.parseInt(c[1]),
                                        c[2], Double.parseDouble(c[3]));
                    if (c.length >= 6) {
                        f.setNota(Double.parseDouble(c[4]));
                        f.setQuantidade_criticos(Integer.parseInt(c[5]));
                    }
                    filmes[qtd++] = f;
                }
            }
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
                } else if (u instanceof Estudante) {
                    tipo = "ESTUDANTE";
                } else {
                    tipo = "USUARIO";
                }

                escritor.println(
                    u.getUser()         + "|" +
                    u.getCpf()          + "|" +
                    u.getSenha()        + "|" +
                    u.getIdade()        + "|" +
                    u.getSexo()         + "|" +
                    u.getEmail()        + "|" +
                    u.getNomecartao()   + "|" +
                    u.getNumerocartao() + "|" +
                    u.getCodigocartao() + "|" +
                    tipo
                );
            }
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
                int    idade = Integer.parseInt(c[3]);
                char   sexo        = c[4].charAt(0);
                String email       = c[5];
                String nomeCartao  = c[6];
                String numCartao   = c[7];
                String codCartao   = c[8];
                String tipoStr     = c.length >= 10 ? c[9] : "USUARIO";

                Usuario u;
                if (tipoStr.startsWith("CRITICO:")) {
                    String origem = tipoStr.substring(8);
                    u = new Critico(user, cpf, senha, idade, sexo, email,
                                    nomeCartao, numCartao, codCartao, origem);
                } else if (tipoStr.equalsIgnoreCase("ESTUDANTE")) {
                    u = new Estudante(user, cpf, senha, idade, sexo, email,
                                      nomeCartao, numCartao, codCartao);
                } else {
                    u = new Usuario(user, cpf, senha, idade, sexo, email,
                                    nomeCartao, numCartao, codCartao);
                }
                usuarios[qtd++] = u;
            }
        } catch (IOException e) {
            System.out.println("Arquivo de usuários não encontrado. Iniciando vazio.");
        }

        qtdLida[0] = qtd;
        return usuarios;
    }
}