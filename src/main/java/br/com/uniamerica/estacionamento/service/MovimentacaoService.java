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
import java.math.RoundingMode;
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

        return this.movimentacaoRepository.save(movimentacao);
    }

    @Transactional
    public ResponseEntity<?> editar(Long id, Movimentacao movimentacao){
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
        String resposta;
        if (movimentacao.getDataSaida() != null){
            //      -------------------------------------------------------------------
            //      CALCULA TEMPO ESTACIONADO

            final Configuracao configuracao = this.configuracaoRepository.getConfiguracao();
            LocalDateTime dataEntrada = movimentacao.getDataEntrada();
            LocalDateTime dataSaida = movimentacao.getDataSaida();

            Duration tempoEstacionado = Duration.between(dataEntrada, dataSaida);

            BigDecimal tempoEstacionadoTotal = BigDecimal.valueOf(tempoEstacionado.toSeconds());

            movimentacao.setTempoEstacionadoSegundos(tempoEstacionadoTotal);
            //      -------------------------------------------------------------------
            //      CALCULA VALOR TEMPO ESTACIONAMENTO
            final BigDecimal valorHora = configuracao.getValorHora();
            System.out.println(valorHora);
            final BigDecimal tempoEstacionadoHoras = tempoEstacionadoTotal.divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
            System.out.println(tempoEstacionadoHoras);
            final BigDecimal valorHoraEstacionada = valorHora.multiply(tempoEstacionadoHoras).setScale(2, BigDecimal.ROUND_HALF_UP);
            System.out.println(valorHoraEstacionada);
            movimentacao.setValorHora(valorHoraEstacionada);
            //      -------------------------------------------------------------------
            //      CALCULA TEMPO MULTA

            long multaSegundos = 0;

            final long ano = movimentacao.getDataSaida().getYear() - movimentacao.getDataEntrada().getYear();
            long dias = movimentacao.getDataSaida().getDayOfYear() - movimentacao.getDataEntrada().getDayOfYear();

            if (ano > 0) {
                dias += 365 * ano;
            }

            if (movimentacao.getDataEntrada().toLocalTime().isBefore(configuracao.getHoraAbertura())) {
                multaSegundos += Duration.between(movimentacao.getDataEntrada().toLocalTime(), configuracao.getHoraAbertura()).toSeconds();
            }

            if (movimentacao.getDataSaida().toLocalTime().isAfter(configuracao.getHoraFechamento())) {
                multaSegundos += Duration.between(configuracao.getHoraFechamento(), movimentacao.getDataSaida().toLocalTime()).toSeconds();
            }

            if (dias > 0) {
                int foraExpediente = (int) Duration.between(configuracao.getHoraAbertura(), configuracao.getHoraFechamento()).toSeconds();
                System.out.println(foraExpediente);
                int duracaoTotal = (int) tempoEstacionado.toSeconds();
                System.out.println(duracaoTotal);
                multaSegundos += duracaoTotal - (dias * foraExpediente);

            }

            BigDecimal tempoMultaSegundos = BigDecimal.valueOf(multaSegundos);

            movimentacao.setTempoMultaSegundos(tempoMultaSegundos);

            // -------------------------------------------------------------------
            // CALCULA VALOR MULTA
            final BigDecimal tempoMultaMinuto = tempoMultaSegundos.divide(BigDecimal.valueOf(60));
            final BigDecimal valorMulta = tempoMultaMinuto.multiply(configuracao.getValorMulta());

            movimentacao.setValorMulta(valorMulta);
            // -------------------------------------------------------------------
            // CALCULA VALOR MULTA
            resposta = String.format(
                    "\t\tMovimentação [ %s ] fechada! \n" +
                            "  ------------------------------------------\n\n" +
                            "\t\t\t Comprovante:\n\n" +
                            "\tMovimentação número [ %s ]\n" +
                            "\tCondutor:  %s \n" +
                            "\tVeículo:  %s  - Placa  %s \n" +
                            "\tTempo de Multa:  %s \n" +
                            "\tTempo Descontado: %s \n" +
                            "\tTempo Total Estacionado:  %s \n" +
                            "\tValor da Multa: R$ %s" +
                            "\tValor Total: R$ %s \n\n" +
                            "\tValor a ser Pago: R$ %s ",
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getId()
            );
        }else{
            resposta = String.format("Movimentação [ %s ] editada com sucesso!", movimentacao.getId());
        }
        this.movimentacaoRepository.save(movimentacao);
        return ResponseEntity.ok(resposta);

    }
    @Transactional
    public ResponseEntity<?> fecharMovimentacao(Long id){
        final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);
        final Configuracao configuracao = this.configuracaoRepository.getConfiguracao();
        Assert.notNull(configuracao, "Sistema não configurado! Faça as configurações!");
        Assert.notNull(movimentacao, String.format("Movimentação com ID [ %s ] não existe!", id));
        String resposta;
        if(movimentacao.getDataSaida() == null) {

            movimentacao.setDataSaida(LocalDateTime.now());

            final LocalDateTime dataEntrada = movimentacao.getDataEntrada();
            final LocalDateTime dataSaida = movimentacao.getDataSaida();
            final Duration tempoEstacionado = Duration.between(dataEntrada, dataSaida);
            final BigDecimal tempoEstacionadoTotal = BigDecimal.valueOf(tempoEstacionado.toSeconds());

            movimentacao.setTempoEstacionadoSegundos(tempoEstacionadoTotal);

            //      -------------------------------------------------------------------
            //      CALCULA MULTA

            long multaSegundos = 0;

            final long ano = movimentacao.getDataSaida().getYear() - movimentacao.getDataEntrada().getYear();
            long dias = movimentacao.getDataSaida().getDayOfYear() - movimentacao.getDataEntrada().getDayOfYear();

            if (ano > 0) {
                dias += 365 * ano;
            }

            if (movimentacao.getDataEntrada().toLocalTime().isBefore(configuracao.getHoraAbertura())) {
                multaSegundos += Duration.between(movimentacao.getDataEntrada().toLocalTime(), configuracao.getHoraAbertura()).toSeconds();
            }

            if (movimentacao.getDataSaida().toLocalTime().isAfter(configuracao.getHoraFechamento())) {
                multaSegundos += Duration.between(configuracao.getHoraFechamento(), movimentacao.getDataSaida().toLocalTime()).toSeconds();
            }

            if (dias > 0) {
                int duracaoExpediente = (int) Duration.between(configuracao.getHoraAbertura(), configuracao.getHoraFechamento()).toSeconds();
                multaSegundos += dias * duracaoExpediente - duracaoExpediente;
            }

            BigDecimal tempoMultaSegundos = BigDecimal.valueOf(multaSegundos);

            movimentacao.setTempoMultaSegundos(tempoMultaSegundos);

            this.movimentacaoRepository.save(movimentacao);
            resposta = String.format(
                    "\t\tMovimentação [ %s ] fechada! \n" +
                            "  ------------------------------------------\n\n" +
                            "\t\t\t Comprovante:\n\n" +
                            "\tMovimentação número [ %s ]\n" +
                            "\tCondutor:  %s \n" +
                            "\tVeículo:  %s  - Placa  %s \n" +
                            "\tTempo de Multa:  %s \n" +
                            "\tTempo Descontado: %s \n" +
                            "\tTempo Total Estacionado:  %s \n" +
                            "\tValor da Multa: R$ %s" +
                            "\tValor Total: R$ %s \n\n" +
                            "\tValor a ser Pago: R$ %s ",
                    movimentacao.getId(),
                    movimentacao.getId(),
                    movimentacao.getCondutor().getNome(),
                    movimentacao.getVeiculo().getModelo().getNome(),
                    movimentacao.getVeiculo().getPlaca(),
                    movimentacao.getTempoMultaSegundos(),
                    movimentacao.getValorTotal(),
                    movimentacao.getTempoEstacionadoSegundos(),
                    movimentacao.getValorMulta(),
                    movimentacao.getValorTotal(),
                    movimentacao.getValorTotal()
            );
        }else{
            resposta = String.format("Movimentação [ %s ] já está fechada!", movimentacao.getId());
        }
        return ResponseEntity.ok(resposta);
    }


    @Transactional
    public ResponseEntity<?> desativar(Long id){
        /*
         * Verifica se a Movimentação informada existe
         * */
        final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);
        Assert.notNull(movimentacao, "Movimentação não encontrada!");

        movimentacao.setAtivo(false);
        this.movimentacaoRepository.save(movimentacao);
        return ResponseEntity.ok( String.format("Movimentação [ %s ] DESATIVADA", movimentacao.getId()));
    }
}
