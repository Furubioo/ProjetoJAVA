import java.io.*;

public class PersistenciaArquivo {
    static final String ARQUIVO_USUARIOS = "usuarios.txt";
    static final String ARQUIVO_FILMES = "filmes.txt";
    static final int MAX = 100;

    public static void salvarFilmes(Filme[] filmes, int qtd) {
        try {
            printWriter escritor = new PrintWriter(new FileWriter(ARQUIVO_FILMES));

            for (int i = 0; i < qtd; i++) {
                Filme f = filmes[i];
                escritor.println(f.getNome() + "|" + f.getDuracao() + "|" + f.getSinopse().replace("|", " ") + f.getValor());
            }

            escritor.close();
            System.out.println("Filmes salvos: " + qtd + "registro(s).");
        
        } catch (IOException e) {
            System.out.println("Erro ao salvar filmes: " +e.getMessage());
        }
    }

    public static Filme[] carregarFilmes(int[] qtdLida) {
        Filme[] filmes = new Filme[MAX];
        int qtd = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_FILMES));
            String linha;

            while ((linha = leitor.readLine()) != null && qtd < MAX) {
                String[] c = linha.split("\\|");

                if (c.length == 4) {
                    filmes[qtd] = new Filme(c[0], Integer.parseInt(c[1]), c[2], Double.parseDouble(c[3]));
                    qtd++;
                }
            }
            leitor.close();
            System.out.println("Filmes carregados: "+qtd+ "registro(s).");

        } catch (IOException e) {
            System.out.println("Arquivo de filmes não encontrado. Iniciando vazio.");
        }

        qtdLida[0] = qtd;
        return filmes;
    }

    public static void salvarUsuarios(Usuario[] usuarios, int qtd) {
        try {
            printWriter escritor = new PrintWriter(new FileWriter(ARQUIVO_USUARIOS));

            for (int i = 0; i < qtd; i++) {
                Usuario u = usuarios[i];

                escritor.println(u.getUser() + "|" + u.getCpf() + "|" + u.getSenha() + "|" + u.getIdade() + "|"
                + u.getSexo() + "|" + u.getEmail() + "|" + u.getNomecartao() + "|" + u.getNumerocartao() + "|" + u.getCodigocartao());
            }

            escritor.close();
            System.out.println("Usuarios salvos: " + qtd + " registro(s).");

        } catch (IOException e) {
            System.out.println("Erro ao salvar usuários: "+e.getMessage());
        }
    }

    public static Usuario[] carregarUsuarios(int[] qtdLida) {
        Usuario[] usuarios = new Usuario[MAX];
        int qtd = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_USUARIOS));
            String linha;

            while ((linha = leitor.readLine()) != null && qtd < MAX) {
                String[] c = linha.split("\\|");

                if (c.length == 9) {
                    usuarios[qtd] = new Usuario(C[0], c[1], c[2], Integer.parseInt(c[3]), c[4].charAt(0),
                    c[5], c[6], c[7], c[8]);
                    qtd++;
                }
            }
            leitor.close();
            System.out.println("Usuários carregados: "+qtd+" registro(s).");

        } catch(IOException e) {
            System.out.println("Arquivo de usuários não encontrado. Iniciando vazio.");
        }

        qtdLida[0] = qtd;
        return usuarios;
    }


}

