package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Entity
@Table(name = "movimentacoes", schema = "public")
public class Movimentacao extends AbstractEntity {
    @Getter @Setter
    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;
    @Getter @Setter
    @Column(name = "data_saida", nullable = false)
    private LocalDateTime dataSaida;
    @Getter @Setter
    @Column(name = "tempo_estacionado", nullable = false)
    private LocalTime tempoEstacionado;
    @Getter @Setter
    @Column(name = "tempo_multa")
    private LocalTime tempoMulta;
    @Getter @Setter
    @Column(name = "veiculo", nullable = false)
    private Veiculo veiculo;
    @Getter @Setter
    @Column(name = "condutor", nullable = false)
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
