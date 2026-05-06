package com.printai.repository;

import com.printai.model.AvaliacaoMaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<AvaliacaoMaker, Long> {
    List<AvaliacaoMaker> findByMaker_Id(Long makerId);
}
