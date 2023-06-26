package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author Jean Moschen
 * */

@Service
public class CondutorService {
    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public Condutor cadastrar(final Condutor condutor){

        final List<Condutor> condutorByCpf = this.condutorRepository.findByCpf(condutor.getCpf());
        Assert.isTrue(condutorByCpf.isEmpty(), String.format("Condutor com CPF [ %s ] já existe!", condutor.getCpf()));

        condutor.setTempoDescontoSegundos(0L);
        condutor.setTempoDescontoUsadoSegundos(0L);
        condutor.setTempoPagoSegundos(0L);
        return this.condutorRepository.save(condutor);
    }

    @Transactional
    public Condutor editar(Long id, Condutor condutor){
        /*
         * Verifica se o condutor existe
         */
        final Condutor condutorBanco = this.condutorRepository.findById(id).orElse(null);
        Assert.notNull(condutorBanco, "Condutor não existe!");

        /*
         * Verifica os condutores coincidem
         */
        Assert.notNull(condutor.getId(), "ID do Condutor não informado no corpo da requisição");
        Assert.isTrue(condutorBanco.getId().equals(condutor.getId()), "Condutor informado não é o mesmo que o condutor a ser atualizado");
        Assert.notNull(condutor.getCadastro(), "Data de Cadastro do Condutor não informado!");

        return this.condutorRepository.save(condutor);
    }

    @Transactional
    public ResponseEntity<?> relatorioPerfil(Long id){
        /*
         * Verifica se o condutor existe
         */
        final Condutor condutor = this.condutorRepository.findById(id).orElse(null);
        Assert.notNull(condutor, "Condutor não existe!");

        final BigDecimal tempoPagoTotal = new BigDecimal(condutor.getTempoPagoSegundos()).divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
        final Integer tempoPagoHoras = tempoPagoTotal.intValue();
        final Integer tempoPagoHorasMinutos= tempoPagoTotal.subtract(BigDecimal.valueOf(tempoPagoHoras)).multiply(BigDecimal.valueOf(60)).intValue();

        final BigDecimal tempoDescontoDisponivel = new BigDecimal(condutor.getTempoDescontoSegundos()).divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
        final Integer tempoDescontoDisponivelHoras = tempoDescontoDisponivel.intValue();
        final Integer tempoDescontoDisponivelMinutos= tempoDescontoDisponivel.subtract(BigDecimal.valueOf(tempoDescontoDisponivelHoras)).multiply(BigDecimal.valueOf(60)).intValue();

        final BigDecimal tempoDescontoUtilizado = new BigDecimal(condutor.getTempoDescontoUsadoSegundos()).divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
        final Integer tempoDescontoUtilizadoHoras = tempoDescontoUtilizado.intValue();
        final Integer tempoDescontoUtilizadoMinutos= tempoDescontoUtilizado.subtract(BigDecimal.valueOf(tempoDescontoUtilizadoHoras)).multiply(BigDecimal.valueOf(60)).intValue();

        final String tempoPagoTotalString = String.format(" %s:%02d h", tempoPagoHoras, tempoPagoHorasMinutos);
        final String tempoDescontoDisponivelString = String.format(" %s:%02d h", tempoDescontoDisponivelHoras, tempoDescontoDisponivelMinutos);
        final String tempoDescontoUtilizadoString = String.format(" %s:%02d h", tempoDescontoUtilizadoHoras, tempoDescontoUtilizadoMinutos);


        String relatorio;

        relatorio = String.format(
                "Dados do Cliente\n" +
                        "--------------------------\n\n" +
                        "Nome: %s\n" +
                        "CPF: %s\n" +
                        "Telefone: %s\n" +
                        "Tempo Pago Total: %s\n" +
                        "Tempo de Desconto Disponivel: %s\n" +
                        "Tempo Total de Desconto Utilizado: %s\n",
                condutor.getNome(),
                condutor.getCpf(),
                condutor.getTelefone(),
                tempoPagoTotalString,
                tempoDescontoDisponivelString,
                tempoDescontoUtilizadoString
        );

        return ResponseEntity.ok(relatorio);

    }

    @Transactional
    public ResponseEntity<?> desativar(Long id){

        /*
         * Verifica se o Condutor informado existe
         * */
        final Condutor condutorBanco = this.condutorRepository.findById(id).orElse(null);
        Assert.notNull(condutorBanco, "Condutor não encontrado!");

        /*
         * Verifica se o Condutor informado está relacionado a uma Movimentação,
         * True: Desativa o cadastro
         * False: Faz o DELETE do registro
         * */
        if(!this.movimentacaoRepository.findByCondutorId(id).isEmpty()){
            condutorBanco.setAtivo(false);
            this.condutorRepository.save(condutorBanco);
            return ResponseEntity.ok( String.format("Condutor [ %s ] DESATIVADO pois está relacionado a movimentações!", condutorBanco.getNome()));
        }else{
            this.condutorRepository.delete(condutorBanco);
            return ResponseEntity.ok(String.format("Condutor [ %s ] DELETADO com sucesso!", condutorBanco.getNome()));
        }
    }
}
