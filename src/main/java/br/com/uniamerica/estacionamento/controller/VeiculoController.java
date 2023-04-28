package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/veiculo")
public class VeiculoController {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @GetMapping("/lista")
    public ResponseEntity<?> listarAll(){
        return ResponseEntity.ok(this.veiculoRepository.findAll());
    }
    @GetMapping("/lista/ativos")
    public ResponseEntity<?> listarAllAtivos(){
        return ResponseEntity.ok(this.veiculoRepository.findAllAtivo());
    }
    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Veiculo veiculo = this.veiculoRepository.findById(id).orElse(null);
        return veiculo == null ? ResponseEntity.badRequest().body("Nenhum veiculo encontrado") : ResponseEntity.ok(veiculo);
    }

    @PostMapping
    public ResponseEntity<?> cadastrarVeiculo(@RequestBody final Veiculo veiculo){
        try {
            this.veiculoRepository.save(veiculo);
            return ResponseEntity.ok("Veiulo cadastrado com sucesso!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarVeiculo(
            @RequestParam("id") final Long id,
            @RequestBody final Veiculo veiculo
    ){
        try{
            final Veiculo veiculoBanco = this.veiculoRepository.findById(id).orElse(null);
            if(veiculoBanco == null || !veiculoBanco.getId().equals(veiculo.getId())){
                throw new RuntimeException("Nenhum veiculo identificado");
            }
            this.veiculoRepository.save(veiculo);
            return ResponseEntity.ok("Veiculo atualizado com sucesso!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}