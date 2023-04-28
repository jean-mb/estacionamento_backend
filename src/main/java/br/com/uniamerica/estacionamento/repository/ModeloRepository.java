package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {
    @Query("from Modelo where nome like :nome")
    public List<Modelo> findByNomeLike(@Param("nome") final String nome);
    @Query("from Modelo where marca.id = :id")
    public List<Modelo> findByMarcaId(@Param("id") final Long id);
    @Query("from Modelo where ativo = true")
    public List<Modelo> findAllAtivo();

}
