package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.*;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@Service
public class MovimentacaoService {
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public Movimentacao cadastrar(final Movimentacao movimentacao){
        /*
         * Verifica se o condutor foi informada
         * */
        Assert.notNull(movimentacao.getCondutor(), "Condutor não informado! Informe o ID do condutor!");
        Assert.notNull(movimentacao.getVeiculo(), "Veiculo não informado! Informe o ID do veiculo!");
        Assert.notNull(movimentacao.getDataEntrada(), "Data de Entrada não informada!");

        /*
         * Verifica se o condutor e o veiculo exitem
         * */
        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId()).orElse(null);
        Assert.notNull(condutor, "Condutor não existe!");
        final Veiculo veiculo = this.veiculoRepository.findById(movimentacao.getVeiculo().getId()).orElse(null);
        Assert.notNull(veiculo, "Veiculo não existe!");

        return this.movimentacaoRepository.save(movimentacao);
    }

    @Transactional
    public Movimentacao editar(Long id, Movimentacao movimentacao){
        /*
         * Verifica se a movimentação existe
         */
        final Movimentacao movimentacaoBanco = this.movimentacaoRepository.findById(id).orElse(null);
        Assert.notNull(movimentacaoBanco, "Movimentação não existe!");

        /*
         * Verifica as movimentações coincidem
         */
        Assert.isTrue(movimentacaoBanco.getId().equals(movimentacao.getId()), "Movimentação informado não é o mesmo que a Movimentação a ser atualizado");

        /*
         * Verifica os campos que são notNull
         * */
        Assert.notNull(movimentacao.getCondutor(), "Condutor não informado! Informe o ID do condutor!");
        Assert.notNull(movimentacao.getVeiculo(), "Veiculo não informado! Informe o ID do veiculo!");
        Assert.notNull(movimentacao.getDataEntrada(), "Data de Entrada não informada!");

        /*
         * Verifica se o condutor e o veiculo exitem
         * */
        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId()).orElse(null);
        Assert.notNull(condutor, "Condutor não existe!");
        final Veiculo veiculo = this.veiculoRepository.findById(movimentacao.getVeiculo().getId()).orElse(null);
        Assert.notNull(veiculo, "Veiculo não existe!");

        return this.movimentacaoRepository.save(movimentacao);

    }

    @Transactional
    public ResponseEntity<?> desativar(Long id){
        /*
         * Verifica se a Movimentação informada existe
         * */
        final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);
        Assert.notNull(movimentacao, "Movimentação não encontrada!");

        movimentacao.setAtivo(false);
        return ResponseEntity.ok( String.format("Movimentação [ %s ] DESATIVADA", movimentacao.getId()));
    }
}
