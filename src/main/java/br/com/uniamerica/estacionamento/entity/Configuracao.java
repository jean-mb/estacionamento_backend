package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    @Column(name = "valor_hora", nullable = false)
    private BigDecimal valorHora;
    @Getter @Setter
    @Column(name = "valor_multa")
    private BigDecimal valorMulta;
    @Getter @Setter
    @Column(name = "horario_abertura", nullable = false)
    private LocalTime horaAbertura;
    @Getter @Setter
    @Column(name = "horario_fechamento", nullable = false)
    private LocalTime horaFechamento;
}
