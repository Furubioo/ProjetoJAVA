import controller.CinemaController;
import controller.FilmeController;
import controller.UsuarioController;
import view.CinemaView;

public class Main {
    public static void main(String[] args) {
        FilmeController filmeController = new FilmeController();
        UsuarioController usuarioController = new UsuarioController();
        CinemaController cinemaController = new CinemaController();

        filmeController.carregarFilmes();
        usuarioController.carregarUsuarios();

        CinemaView view = new CinemaView(cinemaController, filmeController, usuarioController);
        view.iniciar();
    }
}