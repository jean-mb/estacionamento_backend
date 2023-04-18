package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    public List<Marca> findByNome(final String nome);
    @Query("from Marca where nome like :nome")
    public List<Marca> findByNomeLike(@Param("nome") final String nome);
    @Query(value = "select * from marcas where nome like :nome", nativeQuery = true)
    public List<Marca> findByNomeLikeNative(@Param("nome") final String nome);
}
