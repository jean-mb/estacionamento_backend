package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class VeiculoService {
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public Veiculo cadastrar(Veiculo veiculo){
        veiculo.setPlaca(veiculo.getPlaca().trim());
        final List<Veiculo> veiculosByPlaca = this.veiculoRepository.findByPlaca(veiculo.getPlaca());
        Assert.isTrue(veiculosByPlaca.isEmpty(),  String.format("Veiculo com placa [ %s ] já existe!", veiculo.getPlaca()));

        final Modelo modelo = this.modeloRepository.findById(veiculo.getModelo().getId()).orElse(null);
        Assert.notNull(modelo, "Modelo informado não existe!");

        // Verifica se o modelo esta ativo
        Assert.isTrue(modelo.isAtivo(), String.format("Modelo [ %s ] está desativada!", modelo.getNome()));

        return this.veiculoRepository.save(veiculo);
    }
    @Transactional
    public Veiculo editar(Long id, Veiculo veiculo){
        /*
         * Verifica se o veiculo existe
         */
        final Veiculo veiculoBanco = this.veiculoRepository.findById(id).orElse(null);
        Assert.notNull(veiculoBanco, "Veiculo não existe!");
        Assert.notNull(veiculo.getCadastro(), "Data do cadastro não informada!");
        /*
         * Verifica os veiculos coincidem
         */
        Assert.isTrue(veiculoBanco.getId().equals(veiculo.getId()), "Veiculo informado não é o mesmo que o veiculo a ser atualizado");

        veiculo.setPlaca(veiculo.getPlaca().trim());
        final List<Veiculo> veiculosByPlaca = this.veiculoRepository.findByPlaca(veiculo.getPlaca());
        if(!veiculosByPlaca.isEmpty()){
            Assert.isTrue(veiculosByPlaca.get(0).getId().equals(veiculo.getId()),  String.format("Veiculo com placa [ %s ] já existe!", veiculo.getPlaca()));
        }
        /*
         * Verifica se modelo existe
         * */
        Assert.notNull(veiculo.getModelo(), "Modelo não informado!");
        final Modelo modelo = this.modeloRepository.findById(veiculo.getModelo().getId()).orElse(null);
        Assert.notNull(modelo, "Modelo não existe!");

        // Verifica se o modelo esta ativo
        Assert.isTrue(modelo.isAtivo(), String.format("Modelo [ %s ] está desativada!", modelo.getNome()));

        return this.veiculoRepository.save(veiculo);
    }
    @Transactional
    public ResponseEntity<?> desativar(Long id){

        /*
         * Verifica se o Veiculo informado existe
         * */
        final Veiculo veiculoBanco = this.veiculoRepository.findById(id).orElse(null);
        Assert.notNull(veiculoBanco, "Modelo não encontrado!");

        /*
         * Verifica se o Veiculo informado está relacionado a uma Movimentação,
         * True: Desativa o cadastro
         * False: Faz o DELETE do registro
         * */
        if(!this.movimentacaoRepository.findByVeiculoId(id).isEmpty()){
            veiculoBanco.setAtivo(false);
            this.veiculoRepository.save(veiculoBanco);
            return ResponseEntity.ok( String.format("Veiculo com placa [ %s ] DESATIVADO pois está relacionado a movimentações!", veiculoBanco.getPlaca()));
        }else{
            this.veiculoRepository.delete(veiculoBanco);
            return ResponseEntity.ok(String.format("Veiculo com placa [ %s ] DELETADO com sucesso!", veiculoBanco.getPlaca()));
        }
    }
}
