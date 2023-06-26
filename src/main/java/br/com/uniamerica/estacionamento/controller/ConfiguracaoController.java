package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import br.com.uniamerica.estacionamento.service.ConfiguracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracao")
public class ConfiguracaoController {
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Autowired
    private ConfiguracaoService configuracaoService;

    @GetMapping
    public ResponseEntity<?> getConfiguracao(){
        final Configuracao configuracao = this.configuracaoRepository.getConfiguracao();
        return configuracao == null ? ResponseEntity.badRequest().body("Nenhuma configuração encontrada! Configure o sistema.") : ResponseEntity.ok(configuracao);
    }

    @PostMapping
    public ResponseEntity<?> primeiraConfiguracao(@RequestBody @Validated final Configuracao configuracao){
        try {
            final Configuracao configuracaoBanco = this.configuracaoService.cadastrar(configuracao);
            return ResponseEntity.ok("Configuração feita com sucesso!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editar(@RequestBody @Validated final Configuracao configuracao){
        try {
            final Configuracao configuracaoAtualizado = this.configuracaoService.editar(configuracao);
            return ResponseEntity.ok("Configurações atualizadas com sucesso");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
