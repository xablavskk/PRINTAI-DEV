package com.printai.repository;

import com.printai.model.Avaliacao;
import com.printai.model.Maker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByMaker_Id(Long makerId);
}
