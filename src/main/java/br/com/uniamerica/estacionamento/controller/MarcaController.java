package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.service.MarcaService;
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
    private ModeloRepository modeloRepository;

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Marca marca =  this.marcaRepository.findById(id).orElse(null);
        return marca == null ? ResponseEntity.badRequest().body("Nenhuma marca encontrada") : ResponseEntity.ok(marca);
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
            final Marca newMarca = this.marcaService.cadastrar(marca);
            return ResponseEntity.ok(String.format("Marca [ %s ] cadastrada com sucesso!", marca.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarMarca(
            @RequestParam("id") final Long id,
            @RequestBody Marca marca
    ){
        try {
            final Marca marcaAtualizada = this.marcaService.editar(id, marca);
            return ResponseEntity.ok( String.format("Marca [ %s ] atualizada com sucesso", marcaAtualizada.getNome()));
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
