package com.printai.repository;

import com.printai.model.Tecnologia;
import com.printai.model.TecnologiaTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TecnologiaRepository extends JpaRepository<Tecnologia, Long> {
    Optional<Tecnologia> findByNome(TecnologiaTipo nome);
}
