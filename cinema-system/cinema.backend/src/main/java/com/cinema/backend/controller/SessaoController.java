package com.cinema.backend.controller;

import com.cinema.backend.model.*;
import com.cinema.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/sessoes")
@CrossOrigin(origins = "*")
public class SessaoController {

    private final SessaoRepository sessaoRepository;
    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;
    private final CriticaRepository criticaRepository;
    private final UsuarioRepository usuarioRepository;

    public SessaoController(SessaoRepository sessaoRepository,
                            FilmeRepository filmeRepository,
                            SalaRepository salaRepository,
                            CriticaRepository criticaRepository,
                            UsuarioRepository usuarioRepository) {
        this.sessaoRepository = sessaoRepository;
        this.filmeRepository = filmeRepository;
        this.salaRepository = salaRepository;
        this.criticaRepository = criticaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Sessao> listar() {
        return sessaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sessao> buscar(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");
        return sessaoRepository.findById(idSafe)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> body) {
        try {
            Long filmeId = Objects.requireNonNull(longObrigatorio(body, "filmeId"));
            Long salaId = Objects.requireNonNull(longObrigatorio(body, "salaId"));
            String horario = textoObrigatorio(body, "horario");

            Filme filme = filmeRepository.findById(filmeId).orElse(null);
            Sala sala = salaRepository.findById(salaId).orElse(null);

            if (filme == null || sala == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Filme ou sala inexistente."));
            }

            return ResponseEntity.ok(sessaoRepository.save(new Sessao(filme, sala, horario)));
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        try {
            Sessao sessao = sessaoRepository.findById(idSafe).orElse(null);

            if (sessao == null) {
                return ResponseEntity.notFound().build();
            }

            Long filmeId = Objects.requireNonNull(longObrigatorio(body, "filmeId"), "filmeId nao pode ser nulo");
            Long salaId = Objects.requireNonNull(longObrigatorio(body, "salaId"), "salaId nao pode ser nulo");
            String horario = textoObrigatorio(body, "horario");

            Filme filme = filmeRepository.findById(filmeId).orElse(null);
            Sala sala = salaRepository.findById(salaId).orElse(null);

            if (filme == null || sala == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Filme ou sala inexistente."));
            }

            sessao.setFilme(filme);
            sessao.setSala(sala);
            sessao.setHorario(horario);

            return ResponseEntity.ok(sessaoRepository.save(sessao));
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        if (!sessaoRepository.existsById(idSafe)) {
            return ResponseEntity.notFound().build();
        }

        sessaoRepository.deleteById(idSafe);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<Sessao> encerrar(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        return sessaoRepository.findById(idSafe)
                .map(sessao -> {
                    sessao.setEmCartaz(false);
                    return ResponseEntity.ok(sessaoRepository.save(sessao));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comprar")
    @Transactional
    public ResponseEntity<?> comprarBilhete(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        try {
            Sessao sessao = sessaoRepository.findById(idSafe).orElse(null);
            if (sessao == null) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = resolverUsuario(body);
            CupomPromocional cupom = resolverCupom(body);
            int linha = intObrigatorio(body, "linha");
            int coluna = intObrigatorio(body, "coluna");

            validarCompra(sessao, linha, coluna);
            sessao.ocuparCadeira(linha, coluna);
            sessaoRepository.save(sessao);

            return ResponseEntity.ok(criarRespostaBilhete(sessao, usuario, linha, coluna, cupom, body));
        } catch (VendasException | IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{id}/comprar-multiplos")
    @Transactional
    public ResponseEntity<?> comprarMultiplosBilhetes(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        try {
            Sessao sessao = sessaoRepository.findById(idSafe).orElse(null);
            if (sessao == null) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = resolverUsuario(body);
            CupomPromocional cupom = resolverCupom(body);
            List<int[]> assentos = resolverAssentosMultiplos(sessao, body);

            Set<String> repetidos = new HashSet<>();
            for (int[] assento : assentos) {
                String chave = assento[0] + ":" + assento[1];
                if (!repetidos.add(chave)) {
                    throw new IllegalArgumentException("Assento repetido na mesma compra.");
                }
                validarCompra(sessao, assento[0], assento[1]);
            }

            List<Map<String, Object>> bilhetes = new ArrayList<>();
            for (int[] assento : assentos) {
                sessao.ocuparCadeira(assento[0], assento[1]);
                bilhetes.add(criarRespostaBilhete(sessao, usuario, assento[0], assento[1], cupom, body));
            }

            sessaoRepository.save(sessao);

            double precoTotal = bilhetes.stream()
                    .mapToDouble(b -> Double.parseDouble(b.get("precoFinal").toString()))
                    .sum();

            return ResponseEntity.ok(Map.of(
                    "mensagem", "Bilhetes comprados com sucesso.",
                    "quantidade", bilhetes.size(),
                    "bilhetes", bilhetes,
                    "precoTotal", precoTotal
            ));
        } catch (VendasException | IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{id}/critica")
    public ResponseEntity<?> adicionarCritica(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        try {
            Sessao sessao = sessaoRepository.findById(idSafe).orElse(null);
            if (sessao == null) {
                return ResponseEntity.notFound().build();
            }

            Filme filme = sessao.getFilme();
            Long filmeId = filme != null ? filme.getId() : null;
            if (filmeId == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Sessao sem filme."));
            }

            Long filmeIdSafe = Objects.requireNonNull(filmeId);
            if (criticaRepository.findByFilmeId(filmeIdSafe).size() >= Filme.MAX_CRITICAS) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Limite de criticas atingido para este filme."));
            }

            String nomeAutor = texto(body, "nomeAutor", "Anonimo");
            String origem = texto(body, "origem", "");
            String mensagem = textoObrigatorio(body, "mensagem");

            Critica critica = new Critica(nomeAutor, origem, mensagem, filme);
            return ResponseEntity.ok(criticaRepository.save(critica));
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/sala/{salaId}")
    public List<Sessao> listarPorSala(@PathVariable Long salaId) {
        Long salaIdSafe = Objects.requireNonNull(salaId, "salaId nao pode ser nulo");

        return sessaoRepository.findAll().stream()
                .filter(s -> s.getSala() != null && Objects.equals(s.getSala().getId(), salaIdSafe))
                .toList();
    }

    @GetMapping("/filme/{filmeId}")
    public List<Sessao> listarPorFilme(@PathVariable Long filmeId) {
        Long filmeIdSafe = Objects.requireNonNull(filmeId, "filmeId nao pode ser nulo");
        return sessaoRepository.findAll().stream()
                .filter(s -> s.getFilme() != null
                        && Objects.equals(s.getFilme().getId(), filmeIdSafe))
                .toList();
    }

    // ── Validação: remove verificação de horário conforme especificação do projeto ──
    private void validarCompra(Sessao sessao, int linha, int coluna) throws VendasException {
        validarSessao(sessao);

        if (!sessao.posicaoValida(linha, coluna)) {
            throw new IllegalArgumentException("Assento invalido.");
        }

        if (!sessao.cadeiraDisponivel(linha, coluna)) {
            throw new VendasException(VendasException.TipoErro.POLTRONA_OCUPADA);
        }
    }

    private void validarSessao(Sessao sessao) throws VendasException {
        if (!sessao.isEmCartaz()) {
            throw new VendasException(VendasException.TipoErro.SESSAO_JA_PASSOU);
        }
        Filme filme = sessao.getFilme();
        Long filmeId = filme != null ? filme.getId() : null;
        if (filmeId == null || !filmeRepository.existsById(Objects.requireNonNull(filmeId))) {
            throw new VendasException(VendasException.TipoErro.FILME_FORA_DE_CARTAZ);
        }
    }

    private Map<String, Object> criarRespostaBilhete(Sessao sessao, Usuario usuario,
                                                     int linha, int coluna,
                                                     CupomPromocional cupom,
                                                     Map<String, Object> body) {
        double precoBase = sessao.getFilme().getValor();
        double multiplicador = sessao.getSala().getTipo().getMultiplicador();
        double precoSala = precoBase * multiplicador;
        double precoAposUsuario = usuario.calcularPrecoFinal(precoSala);
        double descontoCupomValor = precoAposUsuario * cupom.getDesconto();
        double precoFinal = precoAposUsuario - descontoCupomValor;
        String assento = sessao.formatarCadeira(linha, coluna);

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Bilhete comprado com sucesso.");
        resposta.put("cadeira", assento);
        resposta.put("assento", assento);
        resposta.put("sessaoId", sessao.getId());
        resposta.put("filme", sessao.getFilme().getNome());
        resposta.put("sala", sessao.getSala().getNome());
        resposta.put("tipoSala", sessao.getSala().getTipo().getTipo());
        resposta.put("horario", sessao.getHorario());
        resposta.put("nomeComprador", texto(body, "nomeComprador", usuario.getUser()));
        resposta.put("precoBase", precoBase);
        resposta.put("multiplicador", multiplicador);
        resposta.put("isEstudante", usuario instanceof Estudante);
        resposta.put("isCritico", usuario instanceof Critico);
        resposta.put("cupom", cupom.name());
        resposta.put("descontoCupomValor", descontoCupomValor);
        resposta.put("precoFinal", precoFinal);
        return resposta;
    }

    private Usuario resolverUsuario(Map<String, Object> body) {
        Long usuarioId = Objects.requireNonNull(longObrigatorio(body, "usuarioId"));
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
    }

    private CupomPromocional resolverCupom(Map<String, Object> body) {
        String nomeCupom = texto(body, "cupom", "NENHUM").toUpperCase();

        try {
            return CupomPromocional.valueOf(nomeCupom);
        } catch (IllegalArgumentException e) {
            return CupomPromocional.NENHUM;
        }
    }

    private List<int[]> resolverAssentosMultiplos(Sessao sessao, Map<String, Object> body) {
        // Modo manual: array de {linha, coluna} enviado explicitamente (modo 2 cadeiras)
        Object linhasObj  = body.get("linhas");
        Object colunasObj = body.get("colunas");
        if (linhasObj instanceof List<?> ls && colunasObj instanceof List<?> cs && !ls.isEmpty()) {
            List<int[]> assentos = new ArrayList<>();
            for (int i = 0; i < ls.size(); i++) {
                int linha  = Integer.parseInt(String.valueOf(ls.get(i)));
                int coluna = Integer.parseInt(String.valueOf(cs.get(i)));
                assentos.add(new int[]{linha, coluna});
            }
            return assentos;
        }

        // Modo automático: sugerir N cadeiras juntas (traseira → frente, com variação)
        int quantidade = intObrigatorio(body, "quantidade");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de bilhetes deve ser maior que zero.");
        }

        int[] sugestao = sugerirCadeirasJuntas(sessao, quantidade);
        if (sugestao == null) {
            throw new IllegalArgumentException("Nao ha cadeiras consecutivas suficientes.");
        }

        List<int[]> assentos = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            assentos.add(new int[]{sugestao[0], sugestao[1] + i});
        }
        return assentos;
    }

    /**
     * Sugere N cadeiras consecutivas, priorizando as fileiras de trás (J→A).
     * Dentro de cada fileira elegível, escolhe um bloco aleatório entre todos
     * os blocos disponíveis, para que a sugestão não seja sempre a mesma posição.
     */
    private int[] sugerirCadeirasJuntas(Sessao sessao, int quantidade) {
        boolean[][] cadeiras = sessao.getCadeiras();
        Random rng = new Random();

        // Percorre fileiras de trás para frente (índice maior = fileira mais atrás)
        for (int linha = cadeiras.length - 1; linha >= 0; linha--) {
            List<int[]> blocos = new ArrayList<>();
            int consecutivas = 0;
            int inicio = 0;

            for (int col = 0; col < cadeiras[linha].length; col++) {
                if (!cadeiras[linha][col]) {
                    if (consecutivas == 0) inicio = col;
                    consecutivas++;
                    if (consecutivas >= quantidade) {
                        blocos.add(new int[]{linha, inicio});
                        // Desliza a janela: remove o primeiro da sequência
                        inicio++;
                        consecutivas--;
                    }
                } else {
                    consecutivas = 0;
                }
            }

            if (!blocos.isEmpty()) {
                // Escolhe aleatoriamente entre os blocos disponíveis nesta fileira
                return blocos.get(rng.nextInt(blocos.size()));
            }
        }

        return null;
    }

    private Long longObrigatorio(Map<String, Object> body, String campo) {
        Object val = body == null ? null : body.get(campo);
        if (val == null) {
            throw new IllegalArgumentException("Campo obrigatorio: " + campo);
        }
        try {
            return Long.valueOf(val.toString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + campo + "' deve ser um numero inteiro.");
        }
    }

    private int intObrigatorio(Map<String, Object> body, String campo) {
        Object val = body == null ? null : body.get(campo);
        if (val == null) {
            throw new IllegalArgumentException("Campo obrigatorio: " + campo);
        }
        try {
            return Integer.parseInt(val.toString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + campo + "' deve ser um numero inteiro.");
        }
    }

    private String textoObrigatorio(Map<String, Object> body, String campo) {
        if (body == null || body.get(campo) == null || body.get(campo).toString().isBlank()) {
            throw new IllegalArgumentException("Campo obrigatorio: " + campo);
        }
        return body.get(campo).toString().trim();
    }

    private String texto(Map<String, Object> body, String campo, String padrao) {
        if (body == null || body.get(campo) == null) {
            return padrao;
        }
        String val = body.get(campo).toString().trim();
        return val.isBlank() ? padrao : val;
    }
}