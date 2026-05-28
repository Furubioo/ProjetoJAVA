package com.cinema.backend.repository;

import com.cinema.backend.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    @Transactional
    void deleteByFilmeId(Long filmeId);

    @Transactional
    void deleteBySalaId(Long salaId);
}