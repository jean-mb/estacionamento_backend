package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/marca")
public class MarcaController {
    @Autowired
    private MarcaRepository marcaRepository;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Marca marca =  this.marcaRepository.findById(id).orElse(null);
        return marca == null ? ResponseEntity.badRequest().body("Nenhum modelo encontrado") : ResponseEntity.ok(marca);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> listarAll(){
        return ResponseEntity.ok(this.marcaRepository.findAll());
    }
    @GetMapping("/lista/ativos")
    public ResponseEntity<?> listarAtivos(){
        return ResponseEntity.ok(this.marcaRepository.findAllAtivo());
    }

    @PostMapping
    public ResponseEntity<?> cadastraMarca(@RequestBody final Marca marca){
        try{
            this.marcaRepository.save(marca);
            return ResponseEntity.ok("Marca cadastrada com sucesso");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarMarca(
            @RequestParam("id") final Long id,
            @RequestBody Marca marca
    ){
        try {

            final Marca marcaBanco = this.marcaRepository.findById(id).orElse(null);

            if (marcaBanco == null || !marcaBanco.getId().equals(marca.getId())) {
                throw new RuntimeException("Não foi possivel identifica a marca informada");
            }
            this.marcaRepository.save(marca);
            return ResponseEntity.ok("Marca atualizada com sucesso");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(
            @RequestParam("id") final Long id
    ){
        try{
            final Marca marcaBanco = this.marcaRepository.findById(id).orElse(null);
            assert marcaBanco != null;
            this.marcaRepository.delete(marcaBanco);
            return ResponseEntity.ok("Marca atualizada com sucesso");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
