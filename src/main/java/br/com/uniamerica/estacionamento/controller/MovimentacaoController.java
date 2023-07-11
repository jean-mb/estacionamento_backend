package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.entity.Tipo;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movimentacao")
public class MovimentacaoController {
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private MovimentacaoService movimentacaoService;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);
        return movimentacao == null ? ResponseEntity.badRequest().body("Nenhuma movimentação encontrada") : ResponseEntity.ok(movimentacao);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> listarAll(){
        return ResponseEntity.ok(this.movimentacaoRepository.findAll());
    }

    @GetMapping("/lista/abertas")
    public ResponseEntity<?> listarAbertas(){
        return ResponseEntity.ok(this.movimentacaoRepository.findAllAbertas());
    }
    @GetMapping("/lista/vagas")
    public ResponseEntity<?> getVagas(@RequestParam("tipo") final Tipo tipo){
        return ResponseEntity.ok(this.movimentacaoRepository.getVagas(tipo));
    }

    @PostMapping("/nova")
    public ResponseEntity<?> novaMovimentacao(@RequestBody @Validated final Movimentacao movimentacao){
        try {
            final Movimentacao novaMovimentacao = this.movimentacaoService.novaMovimentacao(movimentacao);
            return ResponseEntity.ok(novaMovimentacao);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editarMovimentacao(
            @RequestParam("id") final Long id,
            @RequestBody @Validated final Movimentacao movimentacao
    ){
        try {
            return  this.movimentacaoService.editar(id, movimentacao);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(
            @RequestParam("id") final Long id
    ){
        try{
            return this.movimentacaoService.desativar(id);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
