package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    @Query("from Veiculo where placa like :placa")
    public List<Veiculo> findByPlacaLike(@Param("placa") final String placa);
    @Query("from Veiculo where modelo.id = :id")
    public List<Veiculo> findByModeloId(@Param("id") final Long id);
    @Query("from Veiculo where ativo = true")
    public List<Veiculo> findAllAtivo();
}
