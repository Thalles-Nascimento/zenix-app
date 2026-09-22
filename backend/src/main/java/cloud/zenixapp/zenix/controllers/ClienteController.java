package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.ClientePlanoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClientePlanosResumoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClienteSimplesPlanosResponseDTO;
import cloud.zenixapp.zenix.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(value = "/${api-url}/clientes")
@Tag(name = "Cliente", description = "API do serviço de Cliente")
public class ClienteController {

    @Value("${api-url}")
    private String apiUrl;
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<Object> criarCliente(@RequestBody @Valid ClienteRequestDTO clienteDTO, BindingResult result){
        log.info("[CONTROLLER -> POST] : ClienteController.criarCliente(Linha: 45) => Endpoint: {POST: /{}/clientes}", apiUrl);
        if (result.hasErrors()){
            Map<String, String> errors = BindingHandler.insertError(result);
            log.error("Corpo da requisição apresentando erros: [{}]", errors);
            log.info("[INSERIR CLIENTE] Response -> {}", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }

        log.info("Inserindo cliente...");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.save(clienteDTO));
    }

    @GetMapping("/telefone/{numero}")
    public ResponseEntity<List<ClienteSimplesPlanosResponseDTO>> buscarClientesPorTelefone(@PathVariable String numero) {
        log.info("[CONTROLLER -> GET(/telefone/[numero])] : ClienteController.buscarClientesPorTelefone(Linha: 62) => Endpoint: {GET: /{}/clientes/telefone/[numero]}", apiUrl);
        log.info("Buscando cliente pelo número do telefone...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.clientesByTelefone(numero));
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
    public ResponseEntity<List<ClientePlanosResumoResponseDTO>> findAllClientes(){
        log.info("[CONTROLLER -> GET] : ClienteController.findAllClientes(Linha: 79) => Endpoint: {GET: /{}/clientes}", apiUrl);
        log.info("Buscando clientes...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.buscarTodosClientes());
    }

    @PatchMapping("/planos/{idCliente}")
    public ResponseEntity<SuccessResponseDTO> vincularPlano(@PathVariable String idCliente, @RequestBody ClientePlanoRequestDTO idPlano){
        log.info("[CONTROLLER -> PATCH(/planos/{idCliente})] : ClienteController.vincularPlano(Linha: 87) => Endpoint: {PATCH: /{}/clientes/planos/[idCliente]}", apiUrl);
        log.info("Vinculando plano para o cliente...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.inserirPlano(idCliente, idPlano));
    }

    @DeleteMapping("/planos/{idCliente}")
    public ResponseEntity<SuccessResponseDTO> desvincularPlano(@PathVariable String idCliente){
        log.info("[CONTROLLER -> DELETE(/planos/{idCliente})] : ClienteController.desvincularPlano(Linha: 95) => Endpoint: {DELETE: /{}/clientes/planos/[idCliente]}", apiUrl);
        log.info("Desvinculando plano do cliente...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.retirarPlano(idCliente));
    }


    /*
     * Endpoint para deletar um cliente do Banco de Dados pelo 'ID'
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente excluído do banco"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @Operation(summary = "Deletar cliente", description = "Endpoint para deletar um cliente")
    public ResponseEntity<SuccessResponseDTO> deleteCliente(@PathVariable String id) {
        log.info("[CONTROLLER -> DELETE(/{id})] : ClienteController.deleteCliente(Linha: 113) => Endpoint: {DELETE: /{}/clientes/[id]}", apiUrl);
        log.info("Deletando cliente...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.deletarCliente(id));

    }

    /*
     * Endpoint para atualizar um cliente do Banco de Dados pelo 'ID'
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Atualizar cliente por ID", description = "Endpoint para atualiza um cliente por ID")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody @Valid ClienteUpdateRequestDTO clienteUpdateRequestDTO, BindingResult result) throws NotFoundException {
        log.info("[CONTROLLER -> PUT(/{id})] : ClienteController.update(Linha: 132) => Endpoint: {PUT: /{}/clientes/[id]}", apiUrl);
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                log.info("Atualizando cliente...");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(clienteService.atualizarCliente(id, clienteUpdateRequestDTO));
            }
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Alguns campos estão fora do padrão",
                    LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant(ZoneOffset.of("-03:00"))
            );
            log.error("Erro ao tentar atualizar cliente: [{}]", error);
            log.info("[ATUALIZAR CLIENTE] Response -> {}", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }

        log.info("Atualizando cliente...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.atualizarCliente(id, clienteUpdateRequestDTO));

    }

    @PatchMapping("/ativar/{id}")
    @Operation(summary = "Ativar cliente", description = "Endpoint para ativar um cliente do sistema")
    public ResponseEntity<SuccessResponseDTO> ativarCliente(@PathVariable String id) {
        log.info("[CONTROLLER -> PATCH(/ativar/{id})] : ClienteController.ativarCliente(Linha: 159) => Endpoint: {PATCH: /{}/clientes/ativar/[id]}", apiUrl);
        log.info("Ativando o cliente...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.ativarCliente(id));
    }

    @GetMapping("/nome")
    public ResponseEntity<ClientePlanosResumoResponseDTO> buscarClientesPorNome(@RequestParam String nome) {
        log.info("[CONTROLLER -> GET(/nome)] : ClienteController.buscarClientesPorNome(Linha: 167) => Endpoint: {GET: /{}/clientes/nome}", apiUrl);
        log.info("Buscando cliente pelo nome...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.clientePorNome(nome));
    }

}
