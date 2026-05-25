package controller;

import model.Administrador;
import model.PersistenciaArquivo;
import model.Usuario;

public class UsuarioController {

    private Usuario[] usuarios;
    private int qtdUsuarios;
    private Administrador adminPadrao;
    private static final int MAX = 100;

    public UsuarioController() {
        this.usuarios = new Usuario[MAX];
        this.qtdUsuarios = 0;
        this.adminPadrao = new Administrador("admin", 0, "admin@cinema.com", 0.0, "ADM001");
    }

    public void carregarUsuarios() {
        int[] qtdLida = new int[1];
        this.usuarios = PersistenciaArquivo.carregarUsuarios(qtdLida);
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
        Administrador admin = new Administrador("sistema", 0, "sistema@cinema.com", 0, "SYS");
        int[] qtd = { qtdUsuarios };
        admin.excluirUsuario(usuarios, qtd, email);
        qtdUsuarios = qtd[0];
        salvarUsuarios();
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

    public Administrador getAdminPadrao() {
        return adminPadrao;
    }

    public void setAdminpadrao(Administrador adminPadrao) {
        this.adminPadrao = adminPadrao;
    }
    
    public int getQtdUsuarios() { 
        return qtdUsuarios; 
    }
}
