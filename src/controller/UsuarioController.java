package controller;

import model.Administrador;
import model.PersistenciaArquivo;
import model.Usuario;

public class UsuarioController {

    private Usuario[] usuarios;
    private int qtdUsuarios;
    private static final int MAX = 100;

    public UsuarioController() {
        this.usuarios    = new Usuario[MAX];
        this.qtdUsuarios = 0;
    }

    public void carregarUsuarios() {
        int[] qtdLida    = new int[1];
        this.usuarios    = PersistenciaArquivo.carregarUsuarios(qtdLida);
        this.qtdUsuarios = qtdLida[0];
    }

    public void salvarUsuarios() {
        Administrador admin = new Administrador("sistema", 0, "sistema@cinema.com", 0, "SYS");
        admin.persistirUsuarios(usuarios, qtdUsuarios);
    }

    public void adicionarUsuario(Usuario usuario) {
        if (qtdUsuarios < MAX) {
            usuarios[qtdUsuarios++] = usuario;
            salvarUsuarios();
        } else {
            System.out.println("Limite de usuários atingido.");
        }
    }

    public void excluirUsuario(String email) {
        for (int i = 0; i < qtdUsuarios; i++) {
            if (usuarios[i].getEmail().equalsIgnoreCase(email)) {
                for (int j = i; j < qtdUsuarios - 1; j++) {
                    usuarios[j] = usuarios[j + 1];
                }
                usuarios[--qtdUsuarios] = null;
                salvarUsuarios();
                System.out.println("Usuário removido: " + email);
                return;
            }
        }
        System.out.println("Usuário não encontrado: " + email);
    }

    public Usuario buscarPorLogin(String user, String senha) {
        for (int i = 0; i < qtdUsuarios; i++)
            if (usuarios[i].getUser().equals(user) && usuarios[i].getSenha().equals(senha))
                return usuarios[i];
        return null;
    }

    public Usuario[] getUsuarios() { 
        return usuarios; 
    }
    public int getQtdUsuarios() { 
        return qtdUsuarios; 
    }
}
