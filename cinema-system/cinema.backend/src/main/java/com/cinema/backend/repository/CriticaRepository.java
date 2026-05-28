package com.cinema.backend.repository;

import com.cinema.backend.model.Critica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriticaRepository extends JpaRepository<Critica, Long> {
    List<Critica> findByFilmeId(Long filmeId);
    List<Critica> findByCriticoIdOrderByIdDesc(Long criticoId);
}