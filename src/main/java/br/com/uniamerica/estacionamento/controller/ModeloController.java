package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/modelo")
public class ModeloController {

    /*
    * Injection
    * */
    @Autowired
    private ModeloRepository modeloRepository;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam("id") final Long id){
        final Modelo modelo = this.modeloRepository.findById(id).orElse(null);

        return modelo == null ? ResponseEntity.badRequest().body("Nenhum modelo encontrado") : ResponseEntity.ok(modelo);
    }

    @GetMapping("/lista ")
    public ResponseEntity<?> listaCompleta(){
        return ResponseEntity.ok(this.modeloRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Modelo modelo){
        try{
            this.modeloRepository.save(modelo);
            return ResponseEntity.ok("Registro feito com sucesso");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /*
    * Sempre recebe parametro
    */
    @PutMapping
    public ResponseEntity<?> editar(
            @RequestParam("id") final Long id,
            @RequestBody final Modelo modelo
    ){
        try{
            final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);

            if (modelo == null || !modeloBanco.getId().equals(modelo.getId())) {
                throw new RuntimeException("Não foi possivel identificar o registro informado");
            }

            this.modeloRepository.save(modelo);
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
            final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);
            assert modeloBanco != null;
            this.modeloRepository.delete(modeloBanco);
            return ResponseEntity.ok("Registro atualizado com sucesso");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
