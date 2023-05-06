package br.com.uniamerica.estacionamento.service;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
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
    @Autowired
    private VeiculoRepository veiculoRepository;

    /**
     * @param modelo Objeto do tipo {@link Modelo} para ser cadastrado.
     * @return Modelo criado, ou em caso de falha em uma verificação, retorna uma Exception
     */
    @Transactional
    public Modelo cadastrar(final Modelo modelo){
        /*
        * Verifica se o nome do modelo foi informado e se contém texto
        * */
        Assert.notNull(modelo.getNome(), "Nome do modelo não informado! Informe o nome do modelo");
        Assert.hasText(modelo.getNome(), "Nome do modelo vazio! Informe o nome do modelo no campo 'nome'!");

        /*
        * Verifica se o nome do modelo já existe
        * */
        final List<Modelo> modelosByNome = this.modeloRepository.findByNome(modelo.getNome());
        Assert.isTrue(modelosByNome.isEmpty(), String.format("Modelo [ %s ] já existe!", modelo.getNome()));

        /*
        * Verifica se a marca foi informada
        * */
        Assert.notNull(modelo.getMarca(), "Marca não informada! Informe o ID da marca!");

        /*
        * Verifica se a marca exite
        * */
        final Marca marca = this.marcaRepository.findById(modelo.getMarca().getId()).orElse(null);
        Assert.notNull(marca, "Marca não existe!");

        return this.modeloRepository.save(modelo);
    }

    /**
     * @param id ID do Modelo a ser editado
     * @param modelo Objeto do tipo {@link Modelo} para editar o registro
     * @return Caso passe por todas as validaçoes, retorna o novo Modelo, senão, retorna uma Exception
     */
    @Transactional
    public Modelo editar(Long id, Modelo modelo){
        /*
        * Verifica se o modelo existe
        */
        final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);
        Assert.notNull(modeloBanco, "Modelo não existe!");

        /*
        * Verifica os modelos coincidem
        */
        Assert.isTrue(modeloBanco.getId().equals(modelo.getId()), "Modelo informado não é o mesmo que o modelo a ser atualizado");

        /*
         * Verifica se o nome do modelo já existe
         * */
        final List<Modelo> modelosByNome = this.modeloRepository.findByNome(modelo.getNome());
        Assert.isTrue(modelosByNome.isEmpty(), String.format("Modelo [ %s ] já existe!", modelo.getNome()));

        /*
        * Verifica os campos que são notNull
        * */
        Assert.notNull(modelo.getCadastro(), "Data do cadastro não informada!");
        Assert.notNull(modelo.getNome(), "Nome do modelo não informado!");
        Assert.hasText(modelo.getNome(), "Nome do modelo vazio! Informe o nome do Modelo no campo 'nome'!");
        Assert.notNull(modelo.getMarca(), "Marca não informada! Informe o ID da Marca");

        /*
        * Verifica se marca existe
        * */
        final Marca marca = this.marcaRepository.findById(modelo.getMarca().getId()).orElse(null);
        Assert.notNull(marca, "Marca não existe!");

        return this.modeloRepository.save(modelo);

    }

    /**
     * @param id ID do {@link Modelo} a ser desativado
     * @return ResponseEntity -> Se o modelo tiver relação com algum Veiculo, desativa o Modelo, senão, faz o DELETE do registro
     */
    @Transactional
    public ResponseEntity<?> desativar(Long id){

        /*
        * Verifica se o Modelo informado existe
        * */
        final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);
        Assert.notNull(modeloBanco, "Modelo não encontrado!");

        /*
        * Verifica se o Modelo informado está relacionado a um Veiculo,
        * True: Desativa o cadastro
        * False: Faz o DELETE do registro
        * */
        if(!this.veiculoRepository.findByModeloId(id).isEmpty()){
            modeloBanco.setAtivo(false);
            this.modeloRepository.save(modeloBanco);
            return ResponseEntity.ok( String.format("Modelo [ %s ] DESATIVADO pois está relacionado a veículos!", modeloBanco.getNome()));
        }else{
            this.modeloRepository.delete(modeloBanco);
            return ResponseEntity.ok(String.format("Modelo [ %s ] DELETADO com sucesso!", modeloBanco.getNome()));
        }
    }
}
