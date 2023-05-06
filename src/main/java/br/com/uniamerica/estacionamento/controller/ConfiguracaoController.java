package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import br.com.uniamerica.estacionamento.service.ConfiguracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/configuracao")
public class ConfiguracaoController {
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Autowired
    private ConfiguracaoService configuracaoService;

    @GetMapping("/lista")
    public ResponseEntity<?> listarAll(){
        return ResponseEntity.ok(this.configuracaoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCondutor(@RequestBody final Configuracao configuracao){
        try {
            final Configuracao configuracaoBanco = this.configuracaoService.cadastrar(configuracao);
            return ResponseEntity.ok("Configuracao cadastrada com sucesso");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editarCondutor(
            @RequestParam("id") final Long id,
            @RequestBody final Configuracao configuracao
    ){
        try{
            final Configuracao configuracaoBanco = this.configuracaoRepository.findById(id).orElse(null);

            if (configuracaoBanco == null || !configuracaoBanco.getId().equals(configuracao.getId())) {
                throw new RuntimeException("Não foi possivel identificar o registro informado");
            }

            this.configuracaoRepository.save(configuracao);
            return ResponseEntity.ok("Registro atualizado com sucesso");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
