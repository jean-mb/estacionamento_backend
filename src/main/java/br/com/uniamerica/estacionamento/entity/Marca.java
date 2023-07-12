package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "marcas", schema = "public")
public class Marca extends AbstractEntity{
    @NotNull(message = "Informe o nome da marca!")
    @NotBlank(message = "O nome da marca foi informado vazio!")
    @Length(min = 2, max = 20, message = "O nome da marca deve ter no mínimo 2 e no máximo 50 caracteres")
    @Getter @Setter
    @Column(name = "nome", nullable = false, unique = true, length = 20)
    private String nome;
}
