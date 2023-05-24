package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class MarcaService {
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ModeloRepository modeloRepository;

    /**
     * @param marca Objeto do tipo {@link Marca} para ser cadastrado
     * @return Objeto do tipo Marca que foi cadastrado no banco
     */
    @Transactional
    public Marca cadastrar(Marca marca){
        /*
        * Verifica se a marca já existe
        * */
        final List<Marca> marcasByNome = this.marcaRepository.findByNome(marca.getNome());
        Assert.isTrue(marcasByNome.isEmpty(), String.format( "Marca [ %s ] já existe!", marca.getNome()));

        return this.marcaRepository.save(marca);
    }
    @Transactional
    public Marca editar(Long id, Marca marca){
        /*
        * Verifica se a Marca existe e se é a mesma que foi informada no body
        * */
        final Marca marcaBanco = this.marcaRepository.findById(id).orElse(null);
        Assert.notNull(marcaBanco, "Marca não encontrada!");
        Assert.isTrue(marcaBanco.getId().equals(marca.getId()), "ID da Marca informada não é a mesmo que o ID da Marca a ser atualizado!");

        /*
         * Verifica se a marca já existe
         * */
        final List<Marca> marcasByNome = this.marcaRepository.findByNome(marca.getNome());

        if(!marcasByNome.isEmpty()){
            Assert.isTrue(marcasByNome.get(0).getId().equals(marca.getId()), String.format("Marca com nome [ %s ] já existe!", marca.getNome()));
        }

        Assert.notNull(marca.getCadastro(), "Data de Cadastro não informada!");
        return this.marcaRepository.save(marca);
    }
    @Transactional
    public ResponseEntity<?> desativar(Long id){
        /*
         * Verifica se a Marca informado existe
         * */
        final Marca marcaBanco = this.marcaRepository.findById(id).orElse(null);
        Assert.notNull(marcaBanco, "Marca não encontrada!");

        /*
         * Verifica se a Marca informado está relacionado a um Modelo,
         * True: Desativa o cadastro
         * False: Faz o DELETE do registro
         * */
        if(!this.modeloRepository.findByMarcaId(id).isEmpty()){
            marcaBanco.setAtivo(false);
            this.marcaRepository.save(marcaBanco);
            return ResponseEntity.ok(String.format("Marca [ %s ] DESATIVADA pois está relacionado a modelos!", marcaBanco.getNome()));
        }else{
            this.marcaRepository.delete(marcaBanco);
            return ResponseEntity.ok(String.format("Marca [ %s ] DELETADA com sucesso!", marcaBanco.getNome()));
        }

    }
}
