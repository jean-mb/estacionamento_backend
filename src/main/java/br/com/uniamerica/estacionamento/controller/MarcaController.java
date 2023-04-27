package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/marca")
public class MarcaController {
    @Autowired
    private MarcaRepository marcaRepository;

    @PostMapping
    public ResponseEntity<?> cadastraMarca(@RequestBody final Marca marca){
        try{
            this.marcaRepository.save(marca);
            return ResponseEntity.ok("Marca cadastrada com sucesso");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getCause().getCause().getMessage());
        }
    }
}
