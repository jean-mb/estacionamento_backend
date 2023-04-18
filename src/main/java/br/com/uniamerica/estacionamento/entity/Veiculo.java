package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="veiculos", schema = "public")
public class Veiculo extends AbstractEntity {
    @Getter @Setter
    @Column(name = "placa", nullable = false, unique = true, length = 10)
    private String placa;
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "cor", nullable = false, length = 15)
    private Cor cor;
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 6)
    private Tipo tipo;
    @Getter @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Modelo modelo;
    @Getter @Setter
    @Column(name = "ano")
    private Integer ano;
}
