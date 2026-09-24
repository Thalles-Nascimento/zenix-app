package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.utils.HelpersLogs;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static cloud.zenixapp.zenix.configs.utils.HelpersVar.CAMADA_CONTROLLER;
import static cloud.zenixapp.zenix.configs.utils.HelpersVar.TIME_ZONE;


@Log4j2
@RestController
@RequestMapping(value = "/${api-url}/clientes")
@Tag(name = "Clientes", description = "Endpoints do Domínio Clientes")
public class ClienteController {

    @Value("${api-url}")
    private String apiUrl;

    // Classe padrão para 'log'
    private static final String CLASS_NAME = "ClienteController";

    // Entidade padrão para 'log'
    private static final String ENTITY_NAME = "Cliente";

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Para clientes inseridos com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Adicionar Cliente", description = "Endpoint para adiciona um novo cliente")
    public ResponseEntity<Object> criarCliente(@RequestBody @Valid ClienteRequestDTO clienteDTO, BindingResult result){
        HelpersLogs.logInfoController(HttpMethod.POST.name(), CLASS_NAME, "criarCliente", apiUrl);

        if (result.hasErrors()){
            Map<String, String> errors = BindingHandler.insertError(result);
            HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.BAD_REQUEST, "Não foi possível inserir o cliente.");
            log.debug("{}.criarCliente => Erros = [{}]", CLASS_NAME, errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }

        log.debug("Inserindo cliente...");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.save(clienteDTO));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.CREATED, "Cliente inserido.");
        return response;
    }

    @GetMapping("/tel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", useReturnTypeSchema = true)
    })
    @Operation(summary = "Listar Clientes por Telefone", description = "Endpoint para listar clientes por telefone")
    public ResponseEntity<List<ClienteSimplesPlanosResponseDTO>> buscarClientesPorTelefone(@RequestParam String tel) {
        HelpersLogs.logInfoController(HttpMethod.GET.name() + "(/tel)", CLASS_NAME, "buscarClientesPorTelefone", apiUrl);

        log.debug("Buscando cliente pelo número do telefone...");
        ResponseEntity<List<ClienteSimplesPlanosResponseDTO>> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.clientesByTelefone(tel));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Clientes encontrados");
        return response;

    }


    /*
     * Endpoint para buscar todos os clientes do Banco de Dados
     *
     */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", useReturnTypeSchema = true)
    })
    @Operation(summary = "Listar Todos os Clientes", description = "Endpoint para listar todos os clientes")
    public ResponseEntity<List<ClientePlanosResumoResponseDTO>> findAllClientes(){
        HelpersLogs.logInfoController(HttpMethod.GET.name(), CLASS_NAME, "findAllClientes", apiUrl);

        log.debug("Buscando clientes...");
        ResponseEntity<List<ClientePlanosResumoResponseDTO>> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.buscarTodosClientes());

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Clientes encontrados");
        return response;

    }


    @PatchMapping("/planos/{idCliente}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano associado ao cliente", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "Cliente já possuo plano", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", useReturnTypeSchema = true)
    })
    @Operation(summary = "Associar Plano ao Cliente", description = "Endpoint para associar um plano ao cliente")
    public ResponseEntity<SuccessResponseDTO> vincularPlano(@PathVariable String idCliente, @RequestBody ClientePlanoRequestDTO idPlano){
        HelpersLogs.logInfoController(HttpMethod.PATCH.name() + "(/planos/[idCliente])", CLASS_NAME, "vincularPlano", apiUrl);

        log.debug("Vinculando plano para o cliente...");
        ResponseEntity<SuccessResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.inserirPlano(idCliente, idPlano));

        assert response.getBody() != null;
        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, response.getBody().message());
        return response;

    }

    @DeleteMapping("/planos/{idCliente}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano retirado do cliente", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "Cliente não possui plano", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", useReturnTypeSchema = true)
    })
    @Operation(summary = "Retirar Plano do Cliente", description = "Endpoint para retirar um plano do cliente")
    public ResponseEntity<SuccessResponseDTO> desvincularPlano(@PathVariable String idCliente){
        HelpersLogs.logInfoController(HttpMethod.DELETE.name() + "(/planos/[idCliente])", CLASS_NAME, "desvincularPlano", apiUrl);

        log.debug("Desvinculando plano do cliente...");
        ResponseEntity<SuccessResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.retirarPlano(idCliente));

        assert response.getBody() != null;
        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, response.getBody().message());
        return response;

    }


    /*
     * Endpoint para deletar um cliente do Banco de Dados pelo 'ID'
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente excluído do banco", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "Não foi possível deletar o cliente", useReturnTypeSchema = true)
    })
    @Operation(summary = "Deletar cliente", description = "Endpoint para deletar um cliente")
    public ResponseEntity<SuccessResponseDTO> deletarCliente(@PathVariable String id) {
        HelpersLogs.logInfoController(HttpMethod.DELETE.name() + "(/[id])", CLASS_NAME, "deletarCliente", apiUrl);

        log.debug("Deletando cliente...");
        ResponseEntity<SuccessResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.deletarCliente(id));

        assert response.getBody() != null;
        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, response.getBody().message());
        return response;

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
    public ResponseEntity<Object> atualizarCliente(@PathVariable String id, @RequestBody @Valid ClienteUpdateRequestDTO clienteUpdateRequestDTO, BindingResult result) {
        HelpersLogs.logInfoController(HttpMethod.PUT.name() + "(/[id])", CLASS_NAME, "atualizarCliente", apiUrl);
        String mensagem = "Cliente atualizado";

        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                log.debug("Atualizando cliente...");
                ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                        .body(clienteService.atualizarCliente(id, clienteUpdateRequestDTO));

                HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, mensagem);
                return response;

            }

            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Alguns campos estão fora do padrão",
                    LocalDateTime.now(TIME_ZONE).toInstant(ZoneOffset.of("-03:00"))
            );

            HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.BAD_REQUEST, "Não foi possível atualizar o cliente.");
            log.debug("ClienteController.atualizarCliente => Erros = [{}]", error);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error);

        }

        log.debug("Atualizando cliente...");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.atualizarCliente(id, clienteUpdateRequestDTO));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, mensagem);
        return response;

    }

    @PatchMapping("/ativar/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento ativado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "Atendimento não está excluído", useReturnTypeSchema = true)
    })
    @Operation(summary = "Ativar cliente", description = "Endpoint para ativar um cliente do sistema")
    public ResponseEntity<SuccessResponseDTO> ativarCliente(@PathVariable String id) {
        HelpersLogs.logInfoController(HttpMethod.PATCH.name() + "(/ativar/[id])", CLASS_NAME, "ativarCliente", apiUrl);

        log.debug("Ativando o cliente...");
        ResponseEntity<SuccessResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.ativarCliente(id));

        assert response.getBody() != null;
        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, response.getBody().message());
        return response;

    }

    @GetMapping("/nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", useReturnTypeSchema = true)
    })
    @Operation(summary = "Listar Cliente por Nome", description = "Endpoint para listar um cliente pelo nome")
    public ResponseEntity<ClientePlanosResumoResponseDTO> buscarClientesPorNome(@RequestParam String nome) {
        HelpersLogs.logInfoController(HttpMethod.GET.name() + "(/nome?nome=" + nome + ")", CLASS_NAME, "buscarClientesPorNome", apiUrl);

        log.debug("Buscando cliente pelo nome...");
        ResponseEntity<ClientePlanosResumoResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.clientePorNome(nome));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Cliente encontrado");
        return response;

    }

}
