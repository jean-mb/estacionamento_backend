package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.service.CondutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/condutor")
public class CondutorController {

    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private CondutorService condutorService;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Condutor condutor = this.condutorRepository.findById(id).orElse(null);
        return condutor == null ? ResponseEntity.badRequest().body("Nenhum condutor encontrado") : ResponseEntity.ok(condutor);
    }
    @GetMapping("/nome")
    public ResponseEntity<?> findByNome(@RequestParam("nome") final String nome){
        final List<Condutor> condutores = this.condutorRepository.findByNome(nome);
        return condutores.isEmpty() ? ResponseEntity.badRequest().body("Nenhum condutor encontrado") : ResponseEntity.ok(condutores);
    }
    @GetMapping("/cpf")
    public ResponseEntity<?> findByCpf(@RequestParam("cpf") final String cpf){
        final List<Condutor> condutores = this.condutorRepository.findByCpf(cpf);
        return condutores.isEmpty() ? ResponseEntity.badRequest().body("Nenhum condutor encontrado") : ResponseEntity.ok(condutores);
    }


    @GetMapping("/relatorio")
    public ResponseEntity<?> relatorio(@RequestParam("id") final Long id){
        return this.condutorService.relatorioPerfil(id);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> listarAll(){
        return ResponseEntity.ok(this.condutorRepository.findAll());
    }

    @GetMapping("/lista/ativos")
    public ResponseEntity<?> listarAtivos(){
        return ResponseEntity.ok(this.condutorRepository.findAllAtivo());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCondutor(@RequestBody @Validated final Condutor condutor){
        try {
            final Condutor newCondutor = this.condutorService.cadastrar(condutor);
            return ResponseEntity.ok(String.format("Condutor [ %s ] cadastrado com sucesso", newCondutor.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editarCondutor(
            @RequestParam("id") final Long id,
            @RequestBody @Validated final Condutor condutor
    ){
        try {
            final Condutor condutorBanco = this.condutorService.editar(id, condutor);
            return ResponseEntity.ok(String.format("Condutor [ %s ] editado com sucesso", condutorBanco.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<?> desativarCondutor(
            @RequestParam("id") final Long id
    ){
        try{
            return this.condutorService.desativar(id);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
