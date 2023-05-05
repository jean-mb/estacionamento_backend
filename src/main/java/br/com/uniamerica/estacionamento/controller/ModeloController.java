package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/modelo")
public class ModeloController {

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Modelo modelo = this.modeloRepository.findById(id).orElse(null);
        return modelo == null ? ResponseEntity.badRequest().body("Nenhum modelo encontrado") : ResponseEntity.ok(modelo);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> listaCompleta(){
        return ResponseEntity.ok(this.modeloRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Modelo modelo){
        try {
            final Modelo newModelo = this.modeloService.cadastrar(modelo);
            return ResponseEntity.ok(String.format("Modelo [ %s ] cadastrado com sucesso", newModelo.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lista/ativos")
    public ResponseEntity<?> listarAtivos(){
        return ResponseEntity.ok(this.modeloRepository.findAllAtivo());
    }

    @PutMapping
    public ResponseEntity<?> editar(
            @RequestParam("id") final Long id,
            @RequestBody final Modelo modelo
    ){
        try {
            final Modelo modeloBanco = this.modeloService.editar(id, modelo);
            return ResponseEntity.ok(String.format("Modelo [ %s ] editado com sucesso", modeloBanco.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<?> desativarCondutor(
            @RequestParam("id") final Long id
    ){
        try{
            final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);
            if(modeloBanco == null){
                throw new RuntimeException("Modelo não encontrado");
            }
            if(!this.veiculoRepository.findByModeloId(id).isEmpty()){
                modeloBanco.setAtivo(false);
                this.modeloRepository.save(modeloBanco);
                return ResponseEntity.ok("Modelo desativado com sucesso!");
            }else{
                this.modeloRepository.delete(modeloBanco);
                return ResponseEntity.ok("Modelo apagado com sucesso!");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
