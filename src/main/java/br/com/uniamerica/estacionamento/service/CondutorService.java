package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class CondutorService {
    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public Condutor cadastrar(final Condutor condutor){

        Assert.notNull(condutor.getNome(), "Nome do condutor não informado! Informe o nome do condutor");
        Assert.hasText(condutor.getNome(), "Nome do condutor vazio! Informe o nome do condutor no campo 'nome'!");
        Assert.isTrue(condutor.getNome().length() <= 50, String.format("Nome do condutor não pode ter mais que 50 caracteres! Nome informado tem %s caracteres!", condutor.getNome().length()));

        Assert.notNull(condutor.getCpf(), "CPF do condutor não informado! Informe o CPF do condutor");
        Assert.hasText(condutor.getCpf(), "CPF do condutor vazio!");
        Assert.isTrue(condutor.getCpf().length() <= 14,String.format("CPF do condutor não pode ter mais que 14 caracteres! CPF informado contem %s caracteres!", condutor.getCpf()));
        final String cpfFormat = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
        Assert.isTrue(condutor.getCpf().matches(cpfFormat), "CPF em formato inválido. O formato deve ser 000.000.000-00");

        final List<Condutor> condutorByCpf = this.condutorRepository.findByCpf(condutor.getCpf());
        Assert.isTrue(condutorByCpf.isEmpty(), String.format("Condutor com CPF [ %s ] já existe!", condutor.getCpf()));

        Assert.notNull(condutor.getTelefone(), "Número de telefone não informado!");
        Assert.hasText(condutor.getTelefone(), "Número de telefone vazio!");
        Assert.isTrue(condutor.getTelefone().length() <= 15, String.format("Telefone do condutor não pode conter mais que 15 caracteres! Telefone informado contem %s caracteres!", condutor.getTelefone().length()));

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
         * Verifica os condutor coincidem
         */
        Assert.isTrue(condutorBanco.getId().equals(condutor.getId()), "Condutor informado não é o mesmo que o condutor a ser atualizado");

        Assert.notNull(condutor.getNome(), "Nome do condutor não informado! Informe o nome do condutor");
        Assert.hasText(condutor.getNome(), "Nome do condutor vazio! Informe o nome do condutor no campo 'nome'!");
        Assert.isTrue(condutor.getNome().length() <= 50, String.format("Nome do condutor não pode ter mais que 50 caracteres! Nome informado tem %s caracteres!", condutor.getNome().length()));

        Assert.notNull(condutor.getCpf(), "CPF do condutor não informado! Informe o CPF do condutor");
        Assert.hasText(condutor.getCpf(), "CPF do condutor vazio!");
        Assert.isTrue(condutor.getCpf().length() <= 14,String.format("CPF do condutor não pode ter mais que 14 caracteres! CPF informado contem %s caracteres!", condutor.getCpf()));
        final String cpfFormat = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
        Assert.isTrue(condutor.getCpf().matches(cpfFormat), "CPF em formato inválido. O formato deve ser 000.000.000-00");

        final List<Condutor> condutorByCpf = this.condutorRepository.findByCpf(condutor.getCpf());
        Assert.isTrue(condutorByCpf.isEmpty(), String.format("Condutor com CPF [ %s ] já existe!", condutor.getCpf()));

        Assert.notNull(condutor.getTelefone(), "Número de telefone não informado!");
        Assert.hasText(condutor.getTelefone(), "Número de telefone vazio!");
        Assert.isTrue(condutor.getTelefone().length() <= 15, String.format("Telefone do condutor não pode conter mais que 15 caracteres! Telefone informado contem %s caracteres!", condutor.getTelefone().length()));

        Assert.notNull(condutor.getCadastro(), "Data de cadastro não informada!");

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
