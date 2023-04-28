package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/movimentacao")
public class MovimentacaoController {
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

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

    @PostMapping
    public ResponseEntity<?> cadastrarMovimentacao(@RequestBody final Movimentacao movimentacao){
        try{
            this.movimentacaoRepository.save(movimentacao);
            return ResponseEntity.ok("Movimentacao feita com sucesso");
        }catch (JpaSystemException e){
            return ResponseEntity.badRequest().body(e.getCause().getCause().getMessage());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarMovimentacao(
            @RequestParam("id") final Long id,
            @RequestBody final Movimentacao movimentacao
    ){
        try{
            final Movimentacao movimentacaoBanco = this.movimentacaoRepository.findById(id).orElse(null);

            if (movimentacaoBanco == null || !movimentacaoBanco.getId().equals(movimentacao.getId())) {
                throw new RuntimeException("Não foi possivel identificar a movimentação informada");
            }

            this.movimentacaoRepository.save(movimentacao);
            return ResponseEntity.ok("Movimentação atualizada com sucesso");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(
            @RequestParam("id") final Long id
    ){
        try{
            final Movimentacao movimentacaoBanco = this.movimentacaoRepository.findById(id).orElse(null);
            if(movimentacaoBanco == null){
                throw new RuntimeException("Movimentação não encontrada!");
            }
            movimentacaoBanco.setAtivo(false);
            this.movimentacaoRepository.save(movimentacaoBanco);
            return ResponseEntity.ok("Movimentação desativada com sucesso!");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
