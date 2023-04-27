package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Condutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CondutorRepository extends JpaRepository<Condutor, Long> {
    @Query("from Condutor where nome like :nome")
    public List<Condutor> findByNomeLike(@Param("nome") final String nome);
    @Query("from Condutor where ativo = true")
    public List<Condutor> findAllAtivo();
}
