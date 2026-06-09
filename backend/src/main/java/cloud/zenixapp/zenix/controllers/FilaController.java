package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.FilaRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.FilaResponseDTO;
import cloud.zenixapp.zenix.models.interfaces.FilaProjectionView;
import cloud.zenixapp.zenix.services.FilaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping(value = "/${api-url}/fila")
@Tag(name = "Fila", description = "API do serviço de Fila")
public class FilaController {

    @Autowired
    private FilaService filaService;

    @PostMapping
    public ResponseEntity<?> inserirClienteFila(@RequestBody @Valid FilaRequestDTO filaDTO, BindingResult result) throws SQLIntegrityConstraintViolationException {
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(filaService.inserirAtendimentoFila(filaDTO));

    }

    @GetMapping
    public ResponseEntity<List<FilaProjectionView>> buscarClientesFila(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(filaService.getFilasByUser());
    }

    @PatchMapping("/chamar/{id}")
    public ResponseEntity<?> chamarClienteFila(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(filaService.chamarCliente(id));
    }


    @PatchMapping("/finalizar/{id}")
    public ResponseEntity<?> finalizarAtendimentoFila(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(filaService.finalizarAtendimento(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarClienteFila(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(filaService.retirarClienteFila(id));
    }

}
