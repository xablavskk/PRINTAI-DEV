package com.printai.repository;

import com.printai.model.MaterialTipo;
import com.printai.model.ServicoImpressao;
import com.printai.model.TecnologiaTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoImpressaoRepository extends JpaRepository<ServicoImpressao, Long> {

    @Query("SELECT DISTINCT s FROM ServicoImpressao s LEFT JOIN FETCH s.maker LEFT JOIN FETCH s.tipo t LEFT JOIN FETCH t.tecnologias LEFT JOIN FETCH s.material")
    List<ServicoImpressao> findAll();

    @Query("SELECT DISTINCT s FROM ServicoImpressao s LEFT JOIN FETCH s.maker LEFT JOIN FETCH s.tipo t LEFT JOIN FETCH t.tecnologias LEFT JOIN FETCH s.material WHERE " +
           "(:tecnologia IS NULL OR EXISTS (SELECT tec FROM Tecnologia tec WHERE tec.tipo = s.tipo AND tec.nome = :tecnologia)) AND " +
           "(:material IS NULL OR (s.material IS NOT NULL AND s.material.nome = :material)) AND " +
           "(:modelo IS NULL OR EXISTS (SELECT i FROM Impressora3D i WHERE i.maker = s.maker AND LOWER(i.modelo) LIKE LOWER(CONCAT('%', :modelo, '%'))))")
    List<ServicoImpressao> buscarAvancado(@Param("tecnologia") TecnologiaTipo tecnologia,
                                          @Param("material") MaterialTipo material,
                                          @Param("modelo") String modelo);

    @Query("SELECT DISTINCT s FROM ServicoImpressao s LEFT JOIN FETCH s.maker LEFT JOIN FETCH s.tipo t LEFT JOIN FETCH t.tecnologias LEFT JOIN FETCH s.material WHERE " +
           "(:pecasPequenas = true AND s.suportaPecasPequenas = true) OR " +
           "(:decorativos = true AND s.suportaDecorativos = true) OR " +
           "(:prototipos = true AND s.suportaPrototipos = true)")
    List<ServicoImpressao> buscarSimplificado(@Param("pecasPequenas") boolean pecasPequenas,
                                              @Param("decorativos") boolean decorativos,
                                              @Param("prototipos") boolean prototipos);
}
