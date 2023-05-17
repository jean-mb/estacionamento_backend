package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.time.LocalTime;
@Entity
@Audited
@AuditTable(value = "condutores_audit", schema = "audit")
@Table(name = "condutores", schema = "public")
public class Condutor extends AbstractEntity {
    @Getter @Setter
    @Column(nullable = false, length = 50)
    private String nome;
    @Getter @Setter
    @Column(nullable = false, length = 14, unique = true)
    private String cpf;
    @Getter @Setter
    @Column(nullable = false, length = 15)
    private String telefone;
    @Getter @Setter
    @Column(name = "tempo_gasto")
    private LocalTime tempoPago;
    @Getter @Setter
    @Column(name = "tempo_desconto")
    private LocalTime tempoDesconto;
}
