package com.cinema.backend.controller;

import com.cinema.backend.model.Critica;
import com.cinema.backend.model.Filme;
import com.cinema.backend.repository.CriticaRepository;
import com.cinema.backend.repository.FilmeRepository;
import com.cinema.backend.repository.SessaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/filmes")
@CrossOrigin(origins = "*")
public class FilmeController {

    private final FilmeRepository filmeRepository;
    private final CriticaRepository criticaRepository;
    private final SessaoRepository sessaoRepository;

    public FilmeController(FilmeRepository filmeRepository,
                           CriticaRepository criticaRepository,
                           SessaoRepository sessaoRepository) {
        this.filmeRepository = filmeRepository;
        this.criticaRepository = criticaRepository;
        this.sessaoRepository = sessaoRepository;
    }

    @GetMapping
    public List<Filme> listar() {
        return filmeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Filme> buscar(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");
        return filmeRepository.findById(idSafe)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Filme filme) {
        Filme filmeSafe = Objects.requireNonNull(filme, "filme nao pode ser nulo");
        return ResponseEntity.ok(filmeRepository.save(filmeSafe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Filme dados) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");
        Filme dadosSafe = Objects.requireNonNull(dados, "dados do filme nao podem ser nulos");

        return filmeRepository.findById(idSafe)
                .map(filme -> {
                    filme.setNome(dadosSafe.getNome());
                    filme.setDuracao(dadosSafe.getDuracao());
                    filme.setSinopse(dadosSafe.getSinopse());
                    filme.setValor(dadosSafe.getValor());

                    if (dadosSafe.getImagemUrl() != null) {
                        filme.setImagemUrl(dadosSafe.getImagemUrl());
                    }

                    return ResponseEntity.ok(filmeRepository.save(filme));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        if (!filmeRepository.existsById(idSafe)) {
            return ResponseEntity.notFound().build();
        }

        sessaoRepository.deleteByFilmeId(idSafe);
        filmeRepository.deleteById(idSafe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/criticas")
    public ResponseEntity<List<Critica>> listarCriticas(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        if (!filmeRepository.existsById(idSafe)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(criticaRepository.findByFilmeId(idSafe));
    }

    @PostMapping("/{id}/nota")
    public ResponseEntity<?> atribuirNota(@PathVariable Long id,
                                          @RequestBody Map<String, Object> body) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        try {
            double nota = Double.parseDouble(textoObrigatorio(body, "nota"));

            if (nota < 0 || nota > 10) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "A nota deve estar entre 0 e 10."));
            }

            return filmeRepository.findById(idSafe)
                    .map(filme -> {
                        double somaAtual = filme.getNota() * filme.getQuantidadeCriticos();
                        filme.setQuantidadeCriticos(filme.getQuantidadeCriticos() + 1);
                        filme.setNota((somaAtual + nota) / filme.getQuantidadeCriticos());
                        return ResponseEntity.ok(filmeRepository.save(filme));
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{id}/critica")
    public ResponseEntity<?> adicionarCritica(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        try {
            Filme filme = filmeRepository.findById(idSafe).orElse(null);

            if (filme == null) {
                return ResponseEntity.notFound().build();
            }

            if (criticaRepository.findByFilmeId(idSafe).size() >= Filme.MAX_CRITICAS) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Limite de criticas atingido para este filme."));
            }

            String nomeAutor = texto(body, "nomeAutor", "Anonimo");
            String origem = texto(body, "origem", "");
            String mensagem = textoObrigatorio(body, "mensagem");

            Long criticoId = longOpcional(body, "criticoId");
            Critica critica = new Critica(nomeAutor, origem, mensagem, filme, criticoId);
            return ResponseEntity.ok(criticaRepository.save(critica));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
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

        String valor = body.get(campo).toString().trim();
        return valor.isBlank() ? padrao : valor;
    }
    @GetMapping("/criticas")
    public ResponseEntity<List<Map<String, Object>>> listarTodasCriticas() {
        List<Map<String, Object>> resposta = criticaRepository.findAll().stream().map(c -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getId());
            item.put("filmeId", c.getFilme() != null ? c.getFilme().getId() : null);
            item.put("filmeNome", c.getFilme() != null ? c.getFilme().getNome() : "Filme removido");
            item.put("nomeAutor", c.getNomeAutor());
            item.put("origem", c.getOrigem());
            item.put("mensagem", c.getMensagem());
            return item;
        }).toList();

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/criticas/{criticaId}")
    @Transactional
    public ResponseEntity<Void> removerCritica(@PathVariable Long criticaId) {
        Long idSafe = Objects.requireNonNull(criticaId, "id da critica nao pode ser nulo");

        if (!criticaRepository.existsById(idSafe)) {
            return ResponseEntity.notFound().build();
        }

        criticaRepository.deleteById(idSafe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/criticas/critico/{criticoId}")
    public ResponseEntity<List<Map<String, Object>>> listarCriticasDoCritico(@PathVariable Long criticoId) {
        Long idSafe = Objects.requireNonNull(criticoId, "id do critico nao pode ser nulo");

        List<Map<String, Object>> resposta = criticaRepository.findByCriticoIdOrderByIdDesc(idSafe)
                .stream()
                .map(this::mapCritica)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    private Map<String, Object> mapCritica(Critica c) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", c.getId());
        item.put("filmeId", c.getFilme() != null ? c.getFilme().getId() : null);
        item.put("filmeNome", c.getFilme() != null ? c.getFilme().getNome() : c.getFilmeNomeSnapshot());
        item.put("nomeAutor", c.getNomeAutor());
        item.put("origem", c.getOrigem());
        item.put("criticoId", c.getCriticoId());
        item.put("mensagem", c.getMensagem());
        return item;
    }
    private Long longOpcional(Map<String, Object> body, String campo) {
        if (body == null || body.get(campo) == null || body.get(campo).toString().isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(body.get(campo).toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
}