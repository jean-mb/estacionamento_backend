package br.com.uniamerica.estacionamento.repository;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    @Query("from Movimentacao where condutor.id = :id")
    public List<Movimentacao> findByCondutorId(@Param("id") final Long id);
    @Query("from Movimentacao where dataSaida = null")
    public List<Movimentacao> findAllAbertas();
    @Query("from Movimentacao where veiculo.id = :id")
    public List<Movimentacao> findByVeiculoId(@Param("id") final Long id);
}
