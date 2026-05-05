package com.printai.repository;

import com.printai.model.Impressora3D;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Impressora3DRepository extends JpaRepository<Impressora3D, Long> {
}
