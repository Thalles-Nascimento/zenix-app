package cloud.zenixapp.test.zenix.controllers;

import cloud.zenixapp.test.zenix.dtos.AtendimentoDTO;
import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.exceptions.AtendimentoException;
import cloud.zenixapp.test.zenix.services.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/atendimento")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    /*
    *Endpoint para inserção de um atendimento no Banco de Dados
    *
    */
    @PostMapping
    @Operation(summary = "Adicionar atendimento", description = "Adiciona um novo atendimento")
    public ResponseEntity<Atendimento> save(@RequestBody AtendimentoDTO atendimentoDTO){
        return ResponseEntity.ok().body(atendimentoService.save(atendimentoDTO));
    }

    /*
    * Endpoint para buscar todos os atendimentos do Banco de Dados
    *
    */
    @GetMapping
    @Operation(summary = "Listar atendimentos", description = "Listar todos os atendimentos")
    public ResponseEntity<@NonNull List<Atendimento>> findAll(){
        return ResponseEntity.ok().body(atendimentoService.findAll());
    }

    /*
     * Endpoint para deletar um atendimento do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletar atendimento", description = "Deletar um atendimento")
    public ResponseEntity<String> deleteAtendimento(@PathVariable Long id) throws AtendimentoException {
        try {
            atendimentoService.delete(id);
            return ResponseEntity.ok().body("Atendimento Excluído!");

        } catch (AtendimentoException e) {
            return ResponseEntity.notFound().build();
        }

    }

    /*
     * Endpoint para buscar um atendimento do Banco de Dados pelo ID
     *
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "Listar atendimento por ID", description = "Lista um atendimento por ID")
    public ResponseEntity<Atendimento> findById(@PathVariable Long id) throws AtendimentoException {
        return ResponseEntity.ok().body(atendimentoService.findById(id));
    }

    /*
     * Endpoint para atualizar um atendimento do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @Operation(summary = "Atualizar atendimento por ID", description = "Atualiza um atendimento por ID")
    public ResponseEntity<Atendimento> update(@PathVariable Long id, @RequestBody AtendimentoDTO atendimentoDTO) throws AtendimentoException {
        return ResponseEntity.ok().body(atendimentoService.update(id, atendimentoDTO));
    }

}
