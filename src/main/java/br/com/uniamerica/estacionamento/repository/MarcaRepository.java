package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    @Query("from Marca where nome = :nome")
    public List<Marca> findByNome(@Param("nome") final String nome);
    @Query("from Marca where ativo = true")
    public List<Marca> findAllAtivo();

}
