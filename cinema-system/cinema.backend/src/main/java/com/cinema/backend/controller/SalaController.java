package com.cinema.backend.controller;

import com.cinema.backend.model.Sala;
import com.cinema.backend.model.TipoSala;
import com.cinema.backend.repository.SalaRepository;
import com.cinema.backend.repository.SessaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/salas")
@CrossOrigin(origins = "*")
public class SalaController {

    private final SalaRepository salaRepository;
    private final SessaoRepository sessaoRepository;

    public SalaController(SalaRepository salaRepository, SessaoRepository sessaoRepository) {
        this.salaRepository = salaRepository;
        this.sessaoRepository = sessaoRepository;
    }

    @GetMapping
    public List<Sala> listar() {
        return salaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sala> buscar(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");
        return salaRepository.findById(idSafe)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> body) {
        try {
            String nome = textoObrigatorio(body, "nome");
            TipoSala tipo = TipoSala.valueOf(textoObrigatorio(body, "tipo").toUpperCase());
            return ResponseEntity.ok(salaRepository.save(new Sala(nome, tipo)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        try {
            Sala sala = salaRepository.findById(idSafe).orElse(null);
            if (sala == null) {
                return ResponseEntity.notFound().build();
            }

            sala.setNome(textoObrigatorio(body, "nome"));
            sala.setTipo(TipoSala.valueOf(textoObrigatorio(body, "tipo").toUpperCase()));
            return ResponseEntity.ok(salaRepository.save(sala));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        if (!salaRepository.existsById(idSafe)) {
            return ResponseEntity.notFound().build();
        }

        sessaoRepository.deleteBySalaId(idSafe);
        salaRepository.deleteById(idSafe);
        return ResponseEntity.noContent().build();
    }

    private String textoObrigatorio(Map<String, Object> body, String campo) {
        if (body == null || body.get(campo) == null || body.get(campo).toString().isBlank()) {
            throw new IllegalArgumentException("Campo obrigatorio: " + campo);
        }
        return body.get(campo).toString().trim();
    }
}