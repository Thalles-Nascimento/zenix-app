package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.FilaRequestDTO;
import cloud.zenixapp.zenix.services.FilaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/fila")
@Tag(name = "Fila", description = "Endpoints do serviço de Fila")
public class FilaController {

    @Autowired
    private FilaService filaService;

    @PostMapping
    public ResponseEntity<?> inserirClienteFila(@RequestBody @Valid FilaRequestDTO filaDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(filaService.inserirAtendimentoFila(filaDTO));

    }

    @GetMapping
    public ResponseEntity<?> buscarAtendimentosFila(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(filaService.getFilasByUser());
    }

}
