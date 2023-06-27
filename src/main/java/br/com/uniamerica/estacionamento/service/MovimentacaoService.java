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
import java.util.List;

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

        final Configuracao configuracao = this.configuracaoRepository.getConfiguracao();
        Assert.notNull(configuracao, "Sistema não está configurado! Faça as configurações antes de abrir uma movimentação");

        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId()).orElse(null);
        Assert.notNull(condutor, "Condutor não existe!");
        Assert.isTrue(condutor.isAtivo(), String.format("Condutor [ %s ] está desativado!", condutor.getNome()));
        final Veiculo veiculo = this.veiculoRepository.findById(movimentacao.getVeiculo().getId()).orElse(null);
        Assert.notNull(veiculo, "Veiculo não existe!");
        Assert.isTrue(veiculo.isAtivo(), String.format("Veiculo [ %s ] está desativado!", veiculo.getPlaca()));

        final List<Movimentacao> isEstacionado = this.movimentacaoRepository.getEstacionado(veiculo.getId());
        Assert.isTrue(isEstacionado.isEmpty(), String.format("O veiculo [ %s ] já está estacionado no momento", veiculo.getPlaca()));

        final Tipo tipo = veiculo.getTipo();
        final List<Movimentacao> vagas = this.movimentacaoRepository.getVagas(tipo);

        if(tipo.name().equals("CARRO")){
            Assert.isTrue(vagas.size() < configuracao.getQntdCarro(), "Não há mais vagas de carros");
        }
        if(tipo.name().equals("MOTO")){
            Assert.isTrue(vagas.size() < configuracao.getQntdMoto(), "Não há mais vagas de moto");
        }
        if(tipo.name().equals("VAN")){
            Assert.isTrue(vagas.size() < configuracao.getQntdCarro(), "Não há mais vagas de van");
        }

        if(movimentacao.getDataSaida() != null){
            Assert.isTrue(movimentacao.getDataEntrada().isBefore(movimentacao.getDataSaida()), "A Data de Saída não pode ser anterior a Data de Entrada!");
        }
        return this.movimentacaoRepository.save(movimentacao);
    }

    @Transactional
    public ResponseEntity<?> editar(Long id, Movimentacao movimentacao){
        /*
         * Verifica se a movimentação existe
         */
        final Configuracao configuracao = this.configuracaoRepository.getConfiguracao();
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
        Assert.isTrue(condutor.isAtivo(), String.format("Condutor [ %s ] está desativado!", condutor.getNome()));
        Assert.isTrue(veiculo.isAtivo(), String.format("Veiculo [ %s ] está desativado!", veiculo.getPlaca()));

        if(movimentacao.getDataSaida() != null){
            Assert.isTrue(movimentacao.getDataEntrada().isBefore(movimentacao.getDataSaida()), "A Data de Saída não pode ser anterior a Data de Entrada!");
        }
        final Tipo tipo = veiculo.getTipo();
        final List<Movimentacao> vagas = this.movimentacaoRepository.getVagas(tipo);
        if(tipo.name().equals("CARRO")){
            Assert.isTrue(vagas.size() < configuracao.getQntdCarro(), "Não há mais vagas de carros");
        }
        if(tipo.name().equals("MOTO")){
            Assert.isTrue(vagas.size() < configuracao.getQntdMoto(), "Não há mais vagas de moto");
        }
        if(tipo.name().equals("VAN")){
            Assert.isTrue(vagas.size() < configuracao.getQntdCarro(), "Não há mais vagas de van");
        }
        String resposta;
        if (movimentacao.getDataSaida() != null){
            resposta = fecharMovimentacao(movimentacao);
            return ResponseEntity.ok(resposta);
        }else{
            resposta = String.format("Movimentação [ %s ] editada com sucesso!", movimentacao.getId());
            this.movimentacaoRepository.save(movimentacao);
            return ResponseEntity.ok(resposta);
        }

    }

    @Transactional
    public String fecharMovimentacao(Movimentacao movimentacao){
        //      -------------------------------------------------------------------
        //      CALCULA TEMPO ESTACIONADO

        final Configuracao configuracao = this.configuracaoRepository.getConfiguracao();
        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId()).orElse(null);
        LocalDateTime dataEntrada = movimentacao.getDataEntrada();
        LocalDateTime dataSaida = movimentacao.getDataSaida();

        Duration tempoEstacionado = Duration.between(dataEntrada, dataSaida);

        long tempoEstacionadoTotal = tempoEstacionado.toSeconds();

        movimentacao.setTempoEstacionadoSegundos(tempoEstacionadoTotal);
        //      -------------------------------------------------------------------
        //      CALCULA VALOR TEMPO ESTACIONAMENTO

        final BigDecimal valorHora = configuracao.getValorHora();

        final BigDecimal tempoEstacionadoHoras = new BigDecimal(tempoEstacionadoTotal).divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);


        //      -------------------------------------------------------------------
        //      CALCULA TEMPO MULTA

        long  multaSegundos = 0;

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
            BigDecimal expedienteSegundos = new BigDecimal(Duration.between(configuracao.getHoraAbertura(), configuracao.getHoraFechamento()).toSeconds());
            long multaTotalDias = BigDecimal.valueOf(24)
                    .multiply(BigDecimal.valueOf(dias *3600))
                    .subtract(expedienteSegundos
                            .multiply(BigDecimal.valueOf(dias))
                            .setScale(2))
                    .longValue();

            multaSegundos +=  multaTotalDias;
        }

        movimentacao.setTempoMultaSegundos(multaSegundos);

        // -------------------------------------------------------------------
        // CALCULA VALOR MULTA
        final BigDecimal tempoMultaMinuto = BigDecimal.valueOf(multaSegundos).divide(BigDecimal.valueOf(60), RoundingMode.CEILING);
        final BigDecimal valorMulta = tempoMultaMinuto.multiply(configuracao.getValorMulta());

        movimentacao.setValorMulta(valorMulta);

        // -------------------------------------------------------------------
        // CALCULA DESCONTO


        assert condutor != null;
        long tempoPagoSegundos = condutor.getTempoPagoSegundos();
        long tempoMovSegundos = movimentacao.getTempoEstacionadoSegundos();
        long tempoParaDesconto = configuracao.getHorasParaDesconto()*3600;
        int multiplicadorAtual = BigDecimal.valueOf(tempoPagoSegundos).divide(BigDecimal.valueOf(tempoParaDesconto), RoundingMode.DOWN).intValue();
        int totalHorasEstacionadasCondutor = BigDecimal.valueOf(tempoPagoSegundos).add(BigDecimal.valueOf(tempoMovSegundos)).intValue();
        int multiplicadorProximo = BigDecimal.valueOf(totalHorasEstacionadasCondutor).divide(BigDecimal.valueOf(tempoParaDesconto), RoundingMode.DOWN).intValue();

        // Se as novas horas pagas vão gerar desconto
        if (multiplicadorProximo  > multiplicadorAtual) {
            long multiplicadorFinal = multiplicadorProximo - multiplicadorAtual;
            long desconto = multiplicadorFinal * configuracao.getHorasDesconto() * 3600;
            long descontoAtual = condutor.getTempoDescontoSegundos();
            long descontoNovo = descontoAtual + desconto;

            // Guarda o desconto no banco de horas
            condutor.setTempoDescontoSegundos(descontoNovo);
            condutor.setTempoPagoSegundos((long) totalHorasEstacionadasCondutor);

        }
        // Se não gerou novas horas, confere se tem direito de abatimento
        else if (condutor.getTempoDescontoSegundos() != 0) {

            // Horas que serão cobradas ( estacionadas - desconto
            long horasCobradas = movimentacao.getTempoEstacionadoSegundos() - condutor.getTempoDescontoSegundos();

            // Horas que sobrarão no banco de horas
            long sobraDesconto;

            // Se as horas de desconto cobrir as horas estacionadas
            if(horasCobradas <= 0){
                System.out.println("if sobra menor que 0");
                sobraDesconto = Math.abs(horasCobradas);
                horasCobradas = 0;
                long tempoDescontoUsadoAnterior = condutor.getTempoDescontoUsadoSegundos();
                condutor.setTempoDescontoUsadoSegundos(tempoDescontoUsadoAnterior + condutor.getTempoDescontoSegundos() - sobraDesconto);
                condutor.setTempoDescontoSegundos(sobraDesconto);
            }
            // Se usar todas as horas de desconto e ainda sobrar horas estacionadas
            else{
                System.out.println("sobra mais que zero");
                sobraDesconto = 0;
                long tempoDescontoUsadoAnterior = condutor.getTempoDescontoUsadoSegundos();
                condutor.setTempoDescontoUsadoSegundos(tempoDescontoUsadoAnterior + tempoMovSegundos - horasCobradas);
                condutor.setTempoDescontoSegundos(sobraDesconto);
            }
            // Aplica o desconto na movimentacao
            long tempoDescontoSegundos = tempoMovSegundos - horasCobradas;
            movimentacao.setTempoDescontoSegundos(tempoDescontoSegundos);

            // Atualiza as horas de desconto utilizadas pelo condutor
//                long tempoDescontoUsadoAnterior = condutor.getTempoDescontoUsadoSegundos();
//                condutor.setTempoDescontoUsadoSegundos(tempoDescontoUsadoAnterior + sobraDesconto);
//                condutor.setTempoDescontoSegundos(sobraDesconto);

            condutor.setTempoPagoSegundos(tempoPagoSegundos + tempoMovSegundos - tempoDescontoSegundos);

        }else{
            System.out.println("ultmimo else");
            long tempoPagoAdicionar = condutor.getTempoPagoSegundos();
            condutor.setTempoPagoSegundos(tempoPagoAdicionar + movimentacao.getTempoEstacionadoSegundos());
        }

        // -------------------------------------------------------------------
        // CALCULA VALOR TOTAL
        final BigDecimal valorHoraSemDesconto = valorHora.multiply(BigDecimal.valueOf(movimentacao.getTempoEstacionadoSegundos()).divide(BigDecimal.valueOf(3600), RoundingMode.CEILING));


        final BigDecimal valorHoraEstacionadaFinal = valorHora.multiply(BigDecimal.valueOf(movimentacao.getTempoEstacionadoSegundos() - movimentacao.getTempoDescontoSegundos()).divide(BigDecimal.valueOf(3600), RoundingMode.CEILING));

        final BigDecimal valorTotal = valorMulta.add(valorHoraEstacionadaFinal);
        final BigDecimal valorTotalSemDesconto = valorMulta.add(valorHoraSemDesconto);
        final BigDecimal valorDescontado = valorTotalSemDesconto.subtract(valorTotal);
        movimentacao.setValorTotal(valorTotal);

        // -------------------------------------------------------------------
        // GERA COMPROVANTE

        final Integer horasEstacionadasComprovante = tempoEstacionadoHoras.intValue();
        final Integer minutosEstacionadosComprovante= tempoEstacionadoHoras.subtract(BigDecimal.valueOf(horasEstacionadasComprovante)).multiply(BigDecimal.valueOf(60)).intValue();

        final BigDecimal tempoDescontoHoras = new BigDecimal(movimentacao.getTempoDescontoSegundos()).divide(BigDecimal.valueOf(3600), RoundingMode.HALF_UP);
        final Integer horasDescontoomprovante = tempoDescontoHoras.intValue();
        final Integer minutosDescontoComprovante= tempoDescontoHoras.subtract(BigDecimal.valueOf(horasDescontoomprovante)).multiply(BigDecimal.valueOf(60)).intValue();

        final String valorDescontadoString = String.format(" %s:%02d h", horasDescontoomprovante, minutosDescontoComprovante);

        final String entradaString = String.format(
                "%02d/%02d/%s - %s:%02d h",
                dataEntrada.getDayOfMonth(),
                dataEntrada.getMonthValue(),
                dataEntrada.getYear(),
                dataEntrada.getHour(),
                dataEntrada.getMinute()
        );

        final String saidaString = String.format(
                "%02d/%02d/%s - %s:%02d h",
                dataSaida.getDayOfMonth(),
                dataSaida.getMonthValue(),
                dataSaida.getYear(),
                dataSaida.getHour(),
                dataSaida.getMinute()
        );
        final String tempoEstacionadoString = String.format("%s:%02d h", horasEstacionadasComprovante, minutosEstacionadosComprovante);

        String resposta = String.format(
                "\t\tMovimentação [ %s ] fechada! \n" +
                        "  ---------------------------------------------------\n\n" +
                        "\t\t\t\t  Comprovante:\n\n" +
                        "\t\t\t\tMovimentação [ %s ]\n\n" +
                        "\tCondutor:  %s \n" +
                        "\tVeículo:  %s -  %s \n\n" +
                        "  ---------------------------------------------------\n\n" +
                        "\tHorário de Entrada: %s\n" +
                        "\tHorário de Saída: %s\n\n" +
                        "\tTempo de Multa: %s minutos \n" +
                        "\tTempo Total Estacionado: %s \n" +
                        "\tTempo Descontado: %s \n\n" +
                        "  ---------------------------------------------------\n\n" +
                        "\tValor da Multa: R$ %s\n" +
                        "\tValor da Hora Estacionada: R$ %s\n" +
                        "\tValor Total: R$ %s \n" +
                        "\tValor Descontado: R$ %s\n\n" +
                        "\tValor a ser Pago: R$ %s ",
                movimentacao.getId(),
                movimentacao.getId(),
                movimentacao.getCondutor().getNome(),
                movimentacao.getVeiculo().getModelo().getNome(),
                movimentacao.getVeiculo().getPlaca(),
                entradaString,
                saidaString,
                tempoMultaMinuto,
                tempoEstacionadoString,
                valorDescontadoString,
                movimentacao.getValorMulta(),
                valorHoraSemDesconto,
                valorTotalSemDesconto,
                valorDescontado,
                movimentacao.getValorTotal()
        );
        this.movimentacaoRepository.save(movimentacao);
        return resposta;
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