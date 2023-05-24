package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {
    @Query("from Configuracao order by id limit 1")
    public Configuracao getConfiguracao();
}
