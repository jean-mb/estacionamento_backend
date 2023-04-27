package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.internal.util.ExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping(value = "/api/condutor")
public class CondutorController {

    @Autowired
    private CondutorRepository condutorRepository;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Condutor condutor = this.condutorRepository.findById(id).orElse(null);

        return condutor == null ? ResponseEntity.badRequest().body("Nenhum condutor encontrado") : ResponseEntity.ok(condutor);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> listaCompleta(){
        return ResponseEntity.ok(this.condutorRepository.findAll());
    }

    @GetMapping("/lista/ativos")
    public ResponseEntity<?> listaCompletaAtivos(){
        return ResponseEntity.ok(this.condutorRepository.findAllAtivo());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Condutor condutor){
        try{
            this.condutorRepository.save(condutor);
            return ResponseEntity.ok("Registro feito com sucesso");
        }catch (JpaSystemException e){
            return ResponseEntity.badRequest().body(e.getCause().getCause().getMessage());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(
            @RequestParam("id") final Long id,
            @RequestBody final Condutor condutor
    ){
        try{
            final Condutor condutorBanco = this.condutorRepository.findById(id).orElse(null);

            if (condutorBanco == null || !condutorBanco.getId().equals(condutor.getId())) {
                throw new RuntimeException("Não foi possivel identificar o condutor informado: \n" + condutorBanco.getId() + "\n" + condutor.getId());
            }

            this.condutorRepository.save(condutor);
            return ResponseEntity.ok("Registro atualizado com sucesso");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<?> deletar(
            @RequestParam("id") final Long id
    ){
        try{
            final Condutor condutorBanco = this.condutorRepository.findById(id).orElse(null);
            assert condutorBanco != null;
            this.condutorRepository.delete(condutorBanco);
            return ResponseEntity.ok("Registro apagado com sucesso!");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
