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
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalTime;
@Entity
@Table(name = "condutores", schema = "public")
public class Condutor extends AbstractEntity {
    @NotNull(message = "Nome do condutor precisa ser informado")
    @NotBlank(message = "Campo do nome do condutor foi informado vazio")
    @Length(min = 2, max = 50, message = "O nome do condutor precisa conter entre 2 e 50 caracteres")
    @Getter @Setter
    @Column(nullable = false, length = 50)
    private String nome;

    @NotNull(message = "CPF do condutor precisa ser informado!")
    @NotBlank(message = "Campo do CPF do condutor foi informado vazio")
    @CPF(message = "CPF inválido")
    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String cpf;

    @NotNull(message = "Número de telefone do condutor precisa ser informado")
    @NotBlank(message = "Número de telefone foi informado vazio!")
    @Length(max = 25, message = "Número do telefone deve conter no máximo 25 caracteres")
    @Getter @Setter
    @Column(nullable = false, length = 25)
    private String telefone;

    @Getter @Setter
    @Column(name = "tempo_gasto_segundos")
    private Long tempoPagoSegundos;

    @Getter @Setter
    @Column(name = "tempo_desconto_segundos")
    private Long tempoDescontoSegundos;

    @Getter @Setter
    @Column(name = "tempo_desconto_usado_segundos")
    private Long tempoDescontoUsadoSegundos;
}
