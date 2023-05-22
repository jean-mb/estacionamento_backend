package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.*;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Service
public class MovimentacaoService {
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;


    @Transactional
    public Movimentacao novaMovimentacao(final Movimentacao movimentacao){

        if (movimentacao.getDataEntrada() == null){
            movimentacao.setDataEntrada(LocalDateTime.now());
        }

        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId()).orElse(null);
        Assert.notNull(condutor, "Condutor não existe!");
        Assert.isTrue(condutor.isAtivo(), String.format("Contudor [ %s ] está desativado!", condutor.getNome()));
        final Veiculo veiculo = this.veiculoRepository.findById(movimentacao.getVeiculo().getId()).orElse(null);
        Assert.notNull(veiculo, "Veiculo não existe!");
        Assert.isTrue(veiculo.isAtivo(), String.format("Veiculo [ %s ] está desativado!", veiculo.getPlaca()));

        final Configuracao configuracao = this.configuracaoRepository.getConfiguracao();
        Assert.notNull(configuracao, "Configuração não encontrada! Configure o sistema antes de usá-lo!");

        final BigDecimal valorHora = configuracao.getValorHora();
        final BigDecimal valorMulta = configuracao.getValorMulta();


        Assert.notNull(valorHora, "Valor hora não configurado! Configure o valor da hora");

        movimentacao.setValorHora(valorHora);
        movimentacao.setValorMulta(valorMulta);

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

        if (movimentacao.getDataSaida() != null){
            LocalDateTime dataEntrada = movimentacao.getDataEntrada();
            LocalDateTime dataSaida = movimentacao.getDataSaida();
            Duration tempoEstacionado = Duration.between(dataEntrada, dataSaida);
            BigDecimal tempoEstacionadoTotal = BigDecimal.valueOf(tempoEstacionado.toSeconds());
            movimentacao.setTempoEstacionadoSegundos(tempoEstacionadoTotal);
        }

        return this.movimentacaoRepository.save(movimentacao);

    }
    @Transactional
    public Movimentacao fecharMovimentacao(Long id){
        final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);
        Assert.notNull(movimentacao, String.format("Movimentação com ID [ %s ] não existe!", id));
        movimentacao.setDataSaida(LocalDateTime.now());

        LocalDateTime dataEntrada = movimentacao.getDataEntrada();
        LocalDateTime dataSaida = movimentacao.getDataSaida();
        Duration tempoEstacionado = Duration.between(dataEntrada, dataSaida);
        BigDecimal tempoEstacionadoTotal = BigDecimal.valueOf(tempoEstacionado.toSeconds());

        movimentacao.setTempoEstacionadoSegundos(tempoEstacionadoTotal);

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
