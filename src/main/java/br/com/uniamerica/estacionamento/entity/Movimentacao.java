package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Audited
@AuditTable(value = "movimentacoes_audit", schema = "audit")
@Table(name = "movimentacoes", schema = "public")
public class Movimentacao extends AbstractEntity {
    @Getter @Setter
    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Getter @Setter
    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Getter @Setter
    @Column(name = "tempo_estacionado")
    private LocalTime tempoEstacionado;

    @Getter @Setter
    @Column(name = "tempo_multa")
    private LocalTime tempoMulta;

    @NotNull(message = "O Veiculo estacionado deve ser informado!")
    @Getter @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Veiculo veiculo;

    @NotNull(message = "O Condutor do Veiculo estacionado deve ser informado!")
    @Getter @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Condutor condutor;

    @Getter @Setter
    @Column(name = "valor_hora")
    private BigDecimal valorHora;

    @Getter @Setter
    @Column(name = "valor_multa")
    private BigDecimal valorMulta;

    @Getter @Setter
    @Column(name = "valor_total")
    private BigDecimal valorTotal;
}
