package com.printai.repository;

import com.printai.model.PrintService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrintServiceRepository extends JpaRepository<PrintService, Long> {

    // Busca Avançada: Busca exata por tecnologia e/ou material
    @Query("SELECT p FROM PrintService p WHERE " +
           "(:technology IS NULL OR p.technology = :technology) AND " +
           "(:material IS NULL OR p.material = :material)")
    List<PrintService> searchAdvanced(@Param("technology") String technology, @Param("material") String material);

    // Busca Simplificada
    @Query("SELECT p FROM PrintService p WHERE " +
           "(:isSmallPiece = true AND p.isSmallPieceCapable = true) OR " +
           "(:isDecorative = true AND p.isDecorativeCapable = true) OR " +
           "(:isPrototype = true AND p.isPrototypeCapable = true)")
    List<PrintService> searchSimplified(
            @Param("isSmallPiece") boolean isSmallPiece,
            @Param("isDecorative") boolean isDecorative,
            @Param("isPrototype") boolean isPrototype);
}
