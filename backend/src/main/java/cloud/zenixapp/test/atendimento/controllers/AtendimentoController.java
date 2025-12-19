package cloud.zenixapp.test.atendimento.controllers;


import cloud.zenixapp.test.atendimento.entities.Atendimento;
import cloud.zenixapp.test.atendimento.services.AtendimentoService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/atendimento")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @PostMapping
    public ResponseEntity<@NonNull Atendimento> save(@RequestBody Atendimento atendimento){
        Atendimento atd = atendimentoService.save(atendimento);
        return ResponseEntity.ok().body(atd);
    }

}
