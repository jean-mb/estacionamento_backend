package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalTime;
@Entity
@Audited
@AuditTable(value = "configuracoes_audit", schema = "audit")
@Table(name = "configuracoes", schema = "public")
public class Configuracao extends AbstractEntity {
    @Getter @Setter
    @Column(name = "qntd_van")
    private int qntdVan;

    @Getter @Setter
    @Column(name = "qntd_carro")
    private int qntdCarro;

    @Getter @Setter
    @Column(name = "qntd_moto")
    private int qntdMoto;

    @Getter @Setter
    @Column(name = "horas_para_desconto")
    private long horasParaDesconto;

    @Getter @Setter
    @Column(name = "horas_desconto")
    private long horasDesconto;


    @NotNull(message = "O valor da hora não pode ser zero!")
    @Getter @Setter
    @Column(name = "valor_hora", nullable = false)
    private BigDecimal valorHora;

    @Getter @Setter
    @Column(name = "valor_multa")
    private BigDecimal valorMulta;

    @NotNull(message = "É necessário informar a hora de abertura!")
    @Getter @Setter
    @Column(name = "horario_abertura", nullable = false)
    private LocalTime horaAbertura;

    @NotNull(message = "É necessário informar a hora de fechamento!")
    @Getter @Setter
    @Column(name = "horario_fechamento", nullable = false)
    private LocalTime horaFechamento;
}
