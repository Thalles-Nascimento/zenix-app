package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.services.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/clientes")
@Tag(name = "Cliente", description = "API do serviço de Cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> criarCliente(@RequestBody @Valid ClienteRequestDTO clienteDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.save(clienteDTO));
    }

    @GetMapping("/telefone/{numero}")
    public ResponseEntity<?> buscarClientesPorTelefone(@PathVariable String numero) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.clientesByTelefone(numero));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarClienteRetorno(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.atualizarRetornoCliente(id));
    }

}
