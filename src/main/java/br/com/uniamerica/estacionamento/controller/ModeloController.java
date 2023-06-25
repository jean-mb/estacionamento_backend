package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/modelo")
public class ModeloController {

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private ModeloService modeloService;

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
    public ResponseEntity<?> cadastrar(@RequestBody @Validated final Modelo modelo){
        try {
            final Modelo newModelo = this.modeloService.cadastrar(modelo);
            return ResponseEntity.ok(String.format("Modelo [ %s ] cadastrado com sucesso", newModelo.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lista/ativos")
    public ResponseEntity<?> listarAtivos(){
        final List<Modelo> modelos = this.modeloRepository.findAllAtivo();
        return modelos.isEmpty() ? ResponseEntity.badRequest().body("Nenhum modelo encontrado! Cadastre-os!") : ResponseEntity.ok(modelos);
    }

    @PutMapping
    public ResponseEntity<?> editar(
            @RequestParam("id") final Long id,
            @RequestBody @Validated final Modelo modelo
    ){
        try {
            final Modelo modeloBanco = this.modeloService.editar(id, modelo);
            return ResponseEntity.ok(String.format("Modelo [ %s ] editado com sucesso", modeloBanco.getNome()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<?> desativar(
            @RequestParam("id") final Long id
    ){
        try{
            return this.modeloService.desativar(id);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
