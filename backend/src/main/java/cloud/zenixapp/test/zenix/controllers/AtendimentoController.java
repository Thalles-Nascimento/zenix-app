package cloud.zenixapp.test.zenix.controllers;

import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.services.AtendimentoService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/zenix")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    /*
    *Endpoint para inserção de um zenix no Banco de Dados
    *
    */
    @PostMapping
    public ResponseEntity<@NonNull Atendimento> save(@RequestBody Atendimento atendimento){
        Atendimento atd = atendimentoService.save(atendimento);
        return ResponseEntity.ok().body(atd);
    }

    /*
    * Endpoint para buscar todos os atendimentos do Banco de Dados
    *
    */
    @GetMapping
    public ResponseEntity<@NonNull List<Atendimento>> findAll(){
        return ResponseEntity.ok().body(atendimentoService.findAll());
    }


}
