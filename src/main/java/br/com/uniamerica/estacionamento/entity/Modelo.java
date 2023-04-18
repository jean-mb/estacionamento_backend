package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "modelos", schema = "public")
public class Modelo extends AbstractEntity {
    @Getter @Setter
    @Column(name = "nome", nullable = false, unique = true)
    private String nome;
    @Getter @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Marca marca;
}
