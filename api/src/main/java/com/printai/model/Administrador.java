package com.printai.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "administradores")
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Administrador extends Usuario {

    public List<Impressora3D> buscarImpressoras() {
        System.out.println("Administrador buscando todas as impressoras cadastradas.");
        return null;
    }

    public void aprovarMaker(Maker maker) {
        System.out.println("Aprovando Maker: " + maker.getNome());
        maker.setStatusAprovacao(true);
    }
}
