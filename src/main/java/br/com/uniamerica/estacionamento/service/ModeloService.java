package br.com.uniamerica.estacionamento.service;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ModeloService {

    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private MarcaRepository marcaRepository;

    @Transactional
    public Modelo cadastrar(final Modelo modelo){
        /*
        * Verifica se o nome do modelo foi informado
        * */
        Assert.notNull(modelo.getNome(), "Nome do modelo não informado!");

        /*
        * Verifica se o nome do modelo já existe
        * */
        final List<Modelo> modelosByNome = this.modeloRepository.findByNome(modelo.getNome());
        Assert.isTrue(modelosByNome.isEmpty(), String.format("Modelo [ %s ] já existe!", modelo.getNome()));

        /*
        * Verifica se a marca foi informada
        * */
        Assert.notNull(modelo.getMarca(), "Marca não informada");

        /*
        * Verifica se a marca exite
        * */
        final Marca marca = this.marcaRepository.findById(modelo.getMarca().getId()).orElse(null);
        Assert.notNull(marca, "Marca não existe!");

        return this.modeloRepository.save(modelo);
    }

    @Transactional
    public Modelo editar(Long id, Modelo modelo){
        /*
        * Verifica se o modelo existe
        */
        final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);
        Assert.notNull(modeloBanco, "Modelo não existe!"    );

        /*
        * Verifica os modelos coincidem
        */
        Assert.isTrue(modeloBanco.getId().equals(modelo.getId()), "Modelo informado não é o mesmo que o modelo a ser atualizado");

        /*
        * Verifica os campos que são notNull
        * */
        Assert.notNull(modelo.getCadastro(), "Data do cadastro não informada!");
        Assert.notNull(modelo.getEdicao(), "Data da edição não informada!");
        Assert.notNull(modelo.getNome(), "Nome do modelo não informado!");
        Assert.notNull(modelo.getMarca(), "Marca não informada");

        /*
        * Verifica se marca existe
        * */
        final Marca marca = this.marcaRepository.findById(modelo.getMarca().getId()).orElse(null);
        Assert.notNull(marca, "Marca não existe!");

        return this.modeloRepository.save(modelo);

    }
}
