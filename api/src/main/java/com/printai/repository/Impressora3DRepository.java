package com.printai.repository;

import com.printai.model.Impressora3D;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Impressora3DRepository extends JpaRepository<Impressora3D, Long> {
    List<Impressora3D> findByModeloContainingIgnoreCase(String modelo);

    List<Impressora3D> findByTecnologiaIgnoreCase(String tecnologia);

    @Query("SELECT i FROM Impressora3D i WHERE " +
           "(:modelo IS NULL OR i.modelo LIKE %:modelo%) AND " +
           "(:tecnologia IS NULL OR i.tecnologia = :tecnologia)")
    List<Impressora3D> buscar(@Param("modelo") String modelo, @Param("tecnologia") String tecnologia);

    @Query("SELECT i FROM Impressora3D i JOIN i.maker m WHERE " +
           "m.latitude BETWEEN :latMin AND :latMax AND " +
           "m.longitude BETWEEN :lonMin AND :lonMax")
    List<Impressora3D> buscarPorRegiao(@Param("latMin") double latMin, @Param("latMax") double latMax, @Param("lonMin") double lonMin, @Param("lonMax") double lonMax);
}
