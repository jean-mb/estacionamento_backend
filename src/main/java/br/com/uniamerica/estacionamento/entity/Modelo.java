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
@Audited
@AuditTable(value = "modelos_audit", schema = "audit")
@Table(name = "modelos", schema = "public")
public class Modelo extends AbstractEntity {
    @NotNull(message = "Nome do modelo deve ser informado!")
    @NotBlank(message = "Nome do modelo foi informado vazio!")
    @Length(min = 2, max = 20, message = "Nome do modelo deve conter entre 2 e 20 caracteres!")
    @Getter @Setter
    @Column(name = "nome", nullable = false, unique = true, length = 20)
    private String nome;

    @NotNull(message = "A Marca do modelo deve ser informada!")
    @Getter @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Marca marca;
}
