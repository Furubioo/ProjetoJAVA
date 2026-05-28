package com.cinema.backend.controller;

import com.cinema.backend.model.*;
import com.cinema.backend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String user = body.get("user");
        String senha = body.get("senha");

        return usuarioRepository.findByUser(user)
                .filter(u -> u.getSenha().equals(senha))
                .map(u -> ResponseEntity.ok((Object) respostaUsuario(u, null)))
                .orElse(ResponseEntity.status(401)
                        .body(Map.of("erro", "Usuario ou senha incorretos.")));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, Object> body) {
        try {
            String tipo = texto(body, "tipo", "COMUM").toUpperCase();

            String user = textoObrigatorio(body, "user");
            String cpfOriginal = textoObrigatorio(body, "cpf");
            String cpf = apenasDigitos(cpfOriginal);
            String senha = textoObrigatorio(body, "senha");

            if (!cpfValido(cpf)) {
                return ResponseEntity.badRequest().body(Map.of("erro", "CPF invalido."));
            }

            if (!senhaValida(senha)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "A senha deve ter pelo menos 8 caracteres, com maiuscula, minuscula e numero."
                ));
            }
            int idade = Integer.parseInt(textoObrigatorio(body, "idade"));

            String sexoStr = texto(body, "sexo", "N");
            char sexo = sexoStr.isEmpty() ? 'N' : sexoStr.charAt(0);

            String email = textoObrigatorio(body, "email");
            String nomeCartao = texto(body, "nomeCartao", "");
            String numeroCartao = texto(body, "numeroCartao", "");
            String codigoCartao = texto(body, "codigoCartao", "");

            if (usuarioRepository.findByUser(user).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nome de usuario ja existe."));
            }

            if (usuarioRepository.findByCpf(cpf).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "CPF ja cadastrado."));
            }

            Usuario novo;

            switch (tipo) {
                case "ESTUDANTE" -> novo = new Estudante(
                        user, cpf, senha, idade, sexo, email,
                        nomeCartao, numeroCartao, codigoCartao);

                case "CRITICO" -> {
                    String origem = texto(body, "origem", "");
                    novo = new Critico(
                            user, cpf, senha, idade, sexo, email,
                            nomeCartao, numeroCartao, codigoCartao, origem);
                }

                case "ADMINISTRADOR" -> {
                    double salario = Double.parseDouble(texto(body, "salario", "0"));
                    String adminId = texto(body, "adminId", "ADM001");

                    novo = new Administrador(
                            user, cpf, senha, idade, sexo, email,
                            nomeCartao, numeroCartao, codigoCartao,
                            salario, adminId);
                }

                default -> novo = new Usuario(
                        user, cpf, senha, idade, sexo, email,
                        nomeCartao, numeroCartao, codigoCartao);
            }

            Usuario salvo = usuarioRepository.save(novo);
            return ResponseEntity.ok(respostaUsuario(salvo, tipo));

        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        Long idSafe = Objects.requireNonNull(id, "id nao pode ser nulo");

        if (!usuarioRepository.existsById(idSafe)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(idSafe);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> respostaUsuario(Usuario u, String fallback) {
        String tipo = u.getDtype();

        if (tipo == null || tipo.isBlank()) {
            if (u instanceof Critico) tipo = "CRITICO";
            else if (u instanceof Estudante) tipo = "ESTUDANTE";
            else if (u instanceof Administrador) tipo = "ADMINISTRADOR";
            else tipo = fallback != null ? fallback : "COMUM";
        }

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("id", u.getId());
        resposta.put("user", u.getUser());
        resposta.put("email", u.getEmail());
        resposta.put("dtype", tipo);
        resposta.put("tipo", tipo);

        if (u instanceof Critico c) {
            resposta.put("origem", c.getOrigem() != null ? c.getOrigem() : "");
        }

        return resposta;
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
    private String apenasDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private boolean senhaValida(String senha) {
        return senha != null && senha.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    private boolean cpfValido(String cpf) {
        String d = apenasDigitos(cpf);
        return d.matches("\\d{11}") && !d.matches("(\\d)\\1{10}");
    }
    // private boolean cpfValido(String cpf) {
    //     if (cpf == null || !cpf.matches("\\d{11}")) return false;
    //     if (cpf.matches("(\\d)\\1{10}")) return false;

    //     int soma = 0;
    //     for (int i = 0; i < 9; i++) {
    //         soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
    //     }

    //     int dig1 = 11 - (soma % 11);
    //     if (dig1 >= 10) dig1 = 0;
    //     if (dig1 != Character.getNumericValue(cpf.charAt(9))) return false;

    //     soma = 0;
    //     for (int i = 0; i < 10; i++) {
    //         soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
    //     }

    //     int dig2 = 11 - (soma % 11);
    //     if (dig2 >= 10) dig2 = 0;

    //     return dig2 == Character.getNumericValue(cpf.charAt(10));
    // }
}