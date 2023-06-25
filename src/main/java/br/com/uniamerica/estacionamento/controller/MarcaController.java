package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/marca")
public class MarcaController {
    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Marca marca =  this.marcaRepository.findById(id).orElse(null);
        return marca == null ? ResponseEntity.badRequest().body("Nenhuma marca encontrada") : ResponseEntity.ok(marca);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> listarAll(){
        final List<Marca> marcas = this.marcaRepository.findAll();
        return marcas.isEmpty() ? ResponseEntity.badRequest().body("Nenhuma marca encontrada! Cadastre-as!") : ResponseEntity.ok(marcas);
    }
    @GetMapping("/lista/ativos")
    public ResponseEntity<?> listarAtivos(){
        final List<Marca> marcas = this.marcaRepository.findAllAtivo();
        return marcas.isEmpty() ? ResponseEntity.badRequest().body("Nenhuma marca encontrada! Cadastre-as!") : ResponseEntity.ok(marcas);
    }

    @PostMapping
    public ResponseEntity<?> cadastrarMarca(@RequestBody @Validated final Marca marca){
        try{
            final Marca newMarca = this.marcaService.cadastrar(marca);
            return ResponseEntity.ok(String.format("Marca [ %s ] cadastrada com sucesso!", marca.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarMarca(
            @RequestParam("id") final Long id,
            @RequestBody @Validated Marca marca
    ){
        try {
            final Marca marcaAtualizada = this.marcaService.editar(id, marca);
            return ResponseEntity.ok( String.format("Marca [ %s ] atualizada com sucesso", marcaAtualizada.getNome()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> desativar(
            @RequestParam("id") final Long id
    ){
        try{
            return this.marcaService.desativar(id);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
