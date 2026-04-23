package com.printai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "print_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String technology; // FDM, SLA, etc.

    @Column(nullable = false)
    private String material; // PLA, ABS, Resin, etc.

    @Column(name = "is_small_piece_capable")
    private Boolean isSmallPieceCapable;

    @Column(name = "is_decorative_capable")
    private Boolean isDecorativeCapable;

    @Column(name = "is_prototype_capable")
    private Boolean isPrototypeCapable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    private User maker;
}
