package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ClienteResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.interfaces.ClientesProjectionView;
import cloud.zenixapp.zenix.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping(value = "/${api-url}/clientes")
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

    // TODO Verificar endpoint
    @GetMapping("/telefone/{numero}")
    public ResponseEntity<?> buscarClientesPorTelefone(@PathVariable String numero) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.clientesByTelefone(numero));
    }

    @PatchMapping("/retorno/{id}")
    public ResponseEntity<?> atualizarClienteRetorno(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.atualizarRetornoCliente(id));
    }

    /*
     * Endpoint para buscar todos os clientes do Banco de Dados
     *
     */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    })
    @Operation(summary = "Listar clientes", description = "Endpoint para listar todos os clientes")
    public ResponseEntity<List<ClientesProjectionView>> findAllClientes(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.buscarTodosClientes());
    }

    @PatchMapping("/planos/{idCliente}")
    public ResponseEntity<?> vincularPlano(@PathVariable String idCliente, @RequestBody String idPlano){
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.inserirPlano(idCliente, idPlano));
    }

    @DeleteMapping("/planos/{idCliente}")
    public ResponseEntity<?> desvincularPlano(@PathVariable String idCliente){
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.retirarPlano(idCliente));
    }


    /*
     * Endpoint para deletar um cliente do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente excluído do banco"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @Operation(summary = "Deletar cliente", description = "Endpoint para deletar um cliente")
    public ResponseEntity<?> deleteCliente(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.deletarCliente(id));

    }

    /*
     * Endpoint para atualizar um cliente do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "404", description = "CLiente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Atualizar cliente por ID", description = "Endpoint para atualiza um cliente por ID")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid ClienteUpdateRequestDTO clienteUpdateRequestDTO, BindingResult result) throws NotFoundException {
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(clienteService.atualizarCliente(id, clienteUpdateRequestDTO));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            "Alguns campos estão fora do padrão",
                            LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.atualizarCliente(id, clienteUpdateRequestDTO));

    }

    @PatchMapping("/ativar/{id}")
    @Operation(summary = "Ativar cliente", description = "Endpoint para ativar um cliente do sistema")
    public ResponseEntity<?> ativarCliente(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.ativarCliente(id));
    }

    @GetMapping("/nome")
    public ResponseEntity<?> buscarClientesPorNome(@RequestParam String nome) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.buscarClientePorNome(nome));
    }

}
