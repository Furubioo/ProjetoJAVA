package com.cinema.backend.repository;

import com.cinema.backend.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FilmeRepository extends JpaRepository<Filme, Long> {
    Optional<Filme> findByNomeIgnoreCase(String nome);
}