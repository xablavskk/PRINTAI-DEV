package com.printai.repository;

import com.printai.model.Material;
import com.printai.model.MaterialTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByNome(MaterialTipo nome);
}
