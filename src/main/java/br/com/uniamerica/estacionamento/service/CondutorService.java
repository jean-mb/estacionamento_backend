package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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

        final Condutor condutorByCpf = this.condutorRepository.findByCpf(condutor.getCpf());
        Assert.isNull(condutorByCpf, String.format("Condutor com CPF [ %s ] já existe!", condutor.getCpf()));
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
        if(!this.movimentacaoRepository.findByVeiculoId(id).isEmpty()){
            condutorBanco.setAtivo(false);
            this.condutorRepository.save(condutorBanco);
            return ResponseEntity.ok( String.format("Condutor [ %s ] DESATIVADO pois está relacionado a movimentações!", condutorBanco.getNome()));
        }else{
            this.condutorRepository.delete(condutorBanco);
            return ResponseEntity.ok(String.format("Condutor [ %s ] DELETADO com sucesso!", condutorBanco.getNome()));
        }
    }
}
