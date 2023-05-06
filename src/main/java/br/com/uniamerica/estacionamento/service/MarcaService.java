package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarcaService {
    @Autowired
    private MarcaRepository marcaRepository;

    /**
     * @param marca Objeto do tipo {@link Marca} para ser cadastrado
     * @return Objeto do tipo Marca que foi cadastrado no banco
     */
    @Transactional
    public Marca cadastrar(Marca marca){
        /*
        * Verifica se o nome da marca foi informado e se contem texto
        * */
        Assert.notNull(marca.getNome(), "Nome da marca não informado! Informe o nome da marca");
        Assert.hasText(marca.getNome(), "Nome da marca está vazio! Escreva o nome da marca no campo 'nome'!");

        /*
        * Verifica se a marca já existe
        * */
        final List<Marca> marcasByNome = this.marcaRepository.findByNome(marca.getNome());
        Assert.isTrue(marcasByNome.isEmpty(), String.format( "Marca já [ %s ] já existe!", marca.getNome()));

        return this.marcaRepository.save(marca);
    }
    @Transactional
    public Marca editar(Long id, Marca marca){
        /*
        * Verifica se a Marca existe e se é a mesma que foi informada no body
        * */
        final Marca marcaBanco = this.marcaRepository.findById(id).orElse(null);
        Assert.notNull(marcaBanco, "Marca não encontrada!");
        Assert.isTrue(marcaBanco.getId().equals(marca.getId()), "ID da Marca informada não é a mesmo que o ID da Marca a ser atualizado!");

        Assert.notNull(marca.getNome(), "Nome da marca não foi informado! Informe o nome da marca com o campo 'nome'");
        Assert.hasText(marca.getNome(), "Nome da marca vazio! Informe o nome da marca no campo 'nome'");
        Assert.notNull(marca.getCadastro(), "Data de cadastro não informada!");
        return this.marcaRepository.save(marca);
    }
//    @Transactional
//    public ResponseEntity<?> desativar(Long id){
//
//    }
}
