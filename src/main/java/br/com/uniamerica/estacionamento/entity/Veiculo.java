package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name="veiculos", schema = "public")
public class Veiculo extends AbstractEntity {
    @NotNull(message = "A placa do Veiculo deve ser informada!")
    @NotBlank(message = "A placa do Veiculo foi informada vazia!")
    @Length(min=5, max = 10, message = "A placa deve conter entre 5 e 10 caracteres!")
    @Getter @Setter
    @Column(name = "placa", nullable = false, unique = true, length = 10)
    private String placa;

    @NotNull(message = "A Cor do Veiculo deve ser informada!")
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "cor", nullable = false, length = 15)
    private Cor cor;

    @NotNull(message = "O tipo do Veiculo deve ser informado!")
    @Getter() @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 6)
    private Tipo tipo;

    @NotNull(message = "O Modelo do Veiculo deve ser informado!")
    @Getter @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Modelo modelo;

    @Getter @Setter
    @Column(name = "ano")
    private Integer ano;
}
