package com.printai.repository;

import com.printai.model.Impressora3D;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Impressora3DRepository extends JpaRepository<Impressora3D, Long> {

    @Override
    @Query("SELECT i FROM Impressora3D i LEFT JOIN FETCH i.maker LEFT JOIN FETCH i.tipo t LEFT JOIN FETCH t.tecnologias LEFT JOIN FETCH i.material")
    List<Impressora3D> findAll();
}
