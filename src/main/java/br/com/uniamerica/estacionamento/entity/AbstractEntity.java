package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@MappedSuperclass // Mapeia para que o banco de dados considere as colunas dessa super-classe
public abstract class AbstractEntity {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false) // Cria a coluna no DB
    private Long id;
    @Getter @Setter
    @Column(nullable = false)
    private LocalDateTime cadastro;
    @Getter @Setter
    @Column()
    private LocalDateTime edicao;
    @Getter @Setter
    @Column(nullable = false)
    private boolean ativo;

    @PrePersist
    public void prePersist() {
        this.cadastro = LocalDateTime.now();
        this.ativo = true;
    }
    @PreUpdate
    public void preUpdate(){
        this.edicao = LocalDateTime.now();
    }
}
