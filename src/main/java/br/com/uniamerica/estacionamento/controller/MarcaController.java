package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/marca")
public class MarcaController {
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ModeloRepository modeloRepository ;

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
    public ResponseEntity<?> cadastrarMarca(@RequestBody final Marca marca){
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
    public ResponseEntity<?> desativarCondutor(
            @RequestParam("id") final Long id
    ){
        try{
            final Marca marcaBanco = this.marcaRepository.findById(id).orElse(null);
            if(marcaBanco == null){
                throw new RuntimeException("Marca não encontrada");
            }
            if(!this.modeloRepository.findByMarcaId(id).isEmpty()){
                marcaBanco.setAtivo(false);
                this.marcaRepository.save(marcaBanco);
                return ResponseEntity.ok("Marca desativada com sucesso!");
            }else{
                this.marcaRepository.delete(marcaBanco);
                return ResponseEntity.ok("Marca apagada com sucesso!");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
