// cinema.backend/src/main/java/com/cinema/backend/DataInitializer.java
// Esta versão é idêntica à anterior (já corrigida) — nenhuma alteração necessária.
// O campo trailerUrl foi decidido NÃO persistir no backend pois os IDs
// do YouTube são mapeados diretamente no frontend (TRAILER_IDS em ProgramacaoPage.jsx),
// evitando dependência de API externa no servidor.
// Se quiser persistir trailerUrl no backend no futuro, basta:
//   1. Adicionar String trailerUrl ao modelo Filme.java
//   2. Passar o ID do YouTube como parâmetro em criarFilme()
//   3. Expor via JSON normalmente

package com.cinema.backend;

import com.cinema.backend.model.*;
import com.cinema.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("all")
public class DataInitializer implements CommandLineRunner {

    private final FilmeRepository filmeRepo;
    private final SalaRepository salaRepo;
    private final SessaoRepository sessaoRepo;
    private final UsuarioRepository usuarioRepo;

    public DataInitializer(
            FilmeRepository filmeRepo,
            SalaRepository salaRepo,
            SessaoRepository sessaoRepo,
            UsuarioRepository usuarioRepo) {
        this.filmeRepo = filmeRepo;
        this.salaRepo = salaRepo;
        this.sessaoRepo = sessaoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public void run(String... args) {
        if (filmeRepo.count() == 0) {
            List<Filme> filmes = List.of(
            criarFilme("Duna: Parte Dois", 167, 26.00,
                "Paul Atreides une-se aos Fremen enquanto busca vingança contra os conspiradores que destruíram sua família. Confrontando uma escolha entre o amor de sua vida e o destino do universo, ele se esforça para evitar um futuro terrível que só ele pode prever.",
                "https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg"),
            criarFilme("Oppenheimer", 181, 28.00,
                "A história do físico americano J. Robert Oppenheimer e seu papel no Projeto Manhattan, que levou ao desenvolvimento da bomba atômica durante a Segunda Guerra Mundial.",
                "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg"),
            criarFilme("Pobres Criaturas", 141, 22.00,
                "A incrível evolução de Bella Baxter, uma jovem trazida de volta à vida pelo brilhante e excêntrico cientista Dr. Godwin Baxter.",
                "https://image.tmdb.org/t/p/w500/kCGlIMHnOm8JPXq3rXM6c5wMxcT.jpg"),
            criarFilme("Missão Impossível: Acerto Final", 164, 30.00,
                "Ethan Hunt e sua equipe do IMF travam uma corrida contra o tempo para rastrear uma assustadora nova arma — uma entidade de inteligência artificial conhecida como A Entidade.",
                "https://image.tmdb.org/t/p/w500/z53D72EAOxGRqdr7KXXWp9dJiDe.jpg"),
            criarFilme("Deadpool e Wolverine", 128, 25.00,
                "Wade Wilson assume o manto do Deadpool e, junto ao relutante Wolverine, embarca numa aventura absurda que ameaça o próprio multiverso.",
                "https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg"),
            criarFilme("Moana 2", 100, 20.00,
                "Moana recebe um chamado inesperado de seus ancestrais e deve velejar para os mares distantes da Oceânia rumo a ilhas perigosas e perdidas.",
                "https://image.tmdb.org/t/p/w500/4Y1h0Sd6SL8xjOJmAdQ3EWQeEG6.jpg"),
            criarFilme("Wicked", 160, 24.00,
                "Uma improvável amizade nasce entre duas estudantes da Universidade de Shiz: a popular Glinda e a incompreendida Elphaba. A história épica que antecede O Mágico de Oz.",
                "https://image.tmdb.org/t/p/w500/xDGbZ0JJ3mYaGKy4Nzd9Kph6M9L.jpg")
        );

            filmeRepo.saveAll(filmes);

            Sala salaComum = criarSala("Sala 1", TipoSala.COMUM);
            Sala sala3D   = criarSala("Sala 2", TipoSala.SALA_3D);
            Sala salaXD   = criarSala("Sala 3", TipoSala.XD);
            Sala salaXD3D = criarSala("Sala 4", TipoSala.XD_3D);

            List<Sala> salas = salaRepo.saveAll(List.of(salaComum, sala3D, salaXD, salaXD3D));

            List<Filme> filmesDB = filmeRepo.findAll();
            String[] horarios = {"14:00", "16:00", "18:00", "20:00", "22:00"};

            for (int i = 0; i < filmesDB.size(); i++) {
                Filme filme = filmesDB.get(i);
                Sala sala   = salas.get(i % salas.size());
                sessaoRepo.save(criarSessao(filme, sala, horarios[i % horarios.length]));
                sessaoRepo.save(criarSessao(filme, sala, horarios[(i + 2) % horarios.length]));
            }

            System.out.println("[DataInitializer] Dados iniciais criados.");
        }

        garantirAdminPadrao();
    }

    private void garantirAdminPadrao() {
        boolean adminExiste = usuarioRepo.findByUser("admin")
                .filter(u -> u instanceof Administrador)
                .isPresent();

        if (!adminExiste) {
            usuarioRepo.findByUser("admin").ifPresent(usuarioRepo::delete);
            usuarioRepo.findByCpf("000.000.000-00").ifPresent(usuarioRepo::delete);
            usuarioRepo.flush();
            usuarioRepo.save(criarAdminPadrao());
            System.out.println("[DataInitializer] Admin criado com dtype=ADMINISTRADOR.");
        }
    }

    private static Administrador criarAdminPadrao() {
        return new Administrador(
            "admin", "000.000.000-00", "admin123", 30, 'M',
            "admin@lumiere.cinema", "", "", "", 0.0, "ADM001"
        );
    }

    private static Filme criarFilme(String nome, int duracao, double valor, String sinopse, String imagemUrl) {
        Filme f = new Filme();
        f.setNome(nome);
        f.setDuracao(duracao);
        f.setValor(valor);
        f.setSinopse(sinopse);
        f.setImagemUrl(imagemUrl);
        f.setNota(0.0);
        f.setQuantidadeCriticos(0);
        return f;
    }

    private static Sala criarSala(String nome, TipoSala tipo) {
        Sala s = new Sala();
        s.setNome(nome);
        s.setTipo(tipo);
        return s;
    }

    private static Sessao criarSessao(Filme filme, Sala sala, String horario) {
        Sessao s = new Sessao();
        s.setFilme(filme);
        s.setSala(sala);
        s.setHorario(horario);
        return s;
    }
}