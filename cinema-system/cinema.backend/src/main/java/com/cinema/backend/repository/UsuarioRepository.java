package com.cinema.backend.repository;

import com.cinema.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUser(String user);
    Optional<Usuario> findByCpf(String cpf);
}