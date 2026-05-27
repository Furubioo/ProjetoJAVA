import controller.CinemaController;
import controller.FilmeController;
import controller.UsuarioController;
import view.CinemaView;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.setErr(new PrintStream(System.err, true, "UTF-8"));

        FilmeController filmeController = new FilmeController();     // ← manter
        UsuarioController usuarioController = new UsuarioController();
        CinemaController cinemaController = new CinemaController();  // ← sem parâmetro agora

        filmeController.carregarFilmes();
        usuarioController.carregarUsuarios();

        CinemaView view = new CinemaView(cinemaController, filmeController, usuarioController);
        view.iniciar();
    }
}