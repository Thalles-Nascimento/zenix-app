package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.ConflictException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.utils.HelpersLogs;
import cloud.zenixapp.zenix.models.dtos.requests.ClientePlanoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClientePlanosResumoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClienteSimplesPlanosResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
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


/**
 * <h2>
 *     Controlador de requisições HTTP para o Domínio Cliente, responsável por expor os endpoints deste.
 * </h2>
 * <p>
 *     Este componente lida com as requisições HTTP mapeadas sob o caminho {@code /api/v2/clientes}
 * </p>
 *
 * Métodos HTTP suportados pelo controller:
 * {@code POST}
 * {@code GET}
 * {@code DELETE}
 * {@code PUT}
 * {@code PATCH}
 * @version 1.0
 * @author Thalles Nascimento
 *
 */
@Log4j2
@RestController
@RequestMapping(value = "/${api-url}/clientes")
@Tag(name = "Clientes", description = "Endpoints do Domínio Cliente")
public class ClienteController {

    @Value("${api-url}")
    private String apiUrl;

    // Classe padrão para 'log'
    private static final String CLASS_NAME = "ClienteController";

    // Entidade padrão para 'log'
    private static final String ENTITY_NAME = "Cliente";

    private final ClienteService clienteService;

    /**
     * <h2>
     *     Construtor padrão para injeção de dependência automatizada pelo Spring.
     * </h2>
     *
     * @param clienteService É o serviço que contêm as regras de negócios do Domínio Cliente.
     *
     * @see ClienteService
     */
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * <h2>
     *     Endpoint para inserir um novo cliente no sistema.
     * </h2>
     *
     * @param clienteDTO DTO responsável pela exposição dos dados necessários para inserção do cliente.
     * @param result Captura os erros de validação. Esses erros são verificados pela anotação @Valid do pacote Jakarta Validation.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 201} ou {@link HttpStatus#BAD_REQUEST 400}.
     *
     * @see ResponseEntity
     * @see SuccessResponseDTO
     * @see HttpStatus
     * @see BindingResult
     */
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

    /**
     * <h2>
     *     Endpoint para listar os clientes dado um celular.
     * </h2>
     *
     * @param cel Celular cadastrado no banco de dados que possui relacionamento com Clientes.
     * @return {@link ResponseEntity} com um List<{@link ClienteSimplesPlanosResponseDTO}> e Status Code {@link HttpStatus#OK 200}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o cliente não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     *
     * @see ResponseEntity
     * @see ClienteSimplesPlanosResponseDTO
     * @see NotFoundException
     * @see HttpStatus
     */
    @GetMapping("/celular")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Clientes não foram encontrados", useReturnTypeSchema = true)
    })
    @Operation(summary = "Listar Clientes por Celular", description = "Endpoint para listar clientes por celular")
    public ResponseEntity<List<ClienteSimplesPlanosResponseDTO>> buscarClientesPorCelular(@RequestParam String cel) {
        HelpersLogs.logInfoController(HttpMethod.GET.name() + "(/cel)", CLASS_NAME, "buscarClientesPorCelular", apiUrl);

        log.debug("Buscando cliente pelo número do celular...");
        ResponseEntity<List<ClienteSimplesPlanosResponseDTO>> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.clientesByCelular(cel));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Clientes encontrados");
        return response;

    }


    /**
     * <h2>
     *     Endpoint para listar todos os clientes.
     * </h2>
     *
     * @return {@link ResponseEntity} com um List<{@link ClientePlanosResumoResponseDTO}> e Status Code {@link HttpStatus#OK 200}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o cliente não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     *
     * @see ResponseEntity
     * @see ClientePlanosResumoResponseDTO
     * @see NotFoundException
     * @see HttpStatus
     */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Clientes não foram encontrados", useReturnTypeSchema = true)
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


    /**
     * <h2>
     *     Endpoint para vincular um plano ao cliente.
     * </h2>
     *
     * @param idCliente 'ID' do cliente que receberá o plano.
     * @param idPlano 'ID' do plano que será inserido ao cliente.
     *
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o cliente já possua um plano - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o cliente não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     *
     * @see ResponseEntity
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     * @see HttpStatus
     */
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

    /**
     * <h2>
     *     Endpoint para desvincular um plano ao cliente.
     * </h2>
     *
     * @param idCliente ID do cliente que perderá o plano.
     *
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o cliente não possua um plano - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o cliente não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     *
     * @see ResponseEntity
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     * @see HttpStatus
     */
    @DeleteMapping("/planos/{idCliente}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano retirado do cliente", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "Cliente não possui plano", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", useReturnTypeSchema = true)
    })
    @Operation(summary = "Desvincular Plano do Cliente", description = "Endpoint para desvincular o plano do cliente")
    public ResponseEntity<SuccessResponseDTO> desvincularPlano(@PathVariable String idCliente){
        HelpersLogs.logInfoController(HttpMethod.DELETE.name() + "(/planos/[idCliente])", CLASS_NAME, "desvincularPlano", apiUrl);

        log.debug("Desvinculando plano do cliente...");
        ResponseEntity<SuccessResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.retirarPlano(idCliente));

        assert response.getBody() != null;
        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, response.getBody().message());
        return response;

    }

    /**
     * <h2>
     *     Endpoint para deletar um cliente.
     * </h2>
     *
     * <p>
     *     O cliente é deletado usando o delete lógico via coluna {@link Clientes#getStatus() Status}.
     * </p>
     *
     * @param id ID do cliente que será excluído.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o cliente já esteja excluído - {@link HttpStatus#CONFLICT 409}.
     *
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see HttpStatus
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


    /**
     * <h2>
     *     Endpoint para atualizar um cliente.
     * </h2>
     *
     * <h5>
     *     Obs: há uma validação para os campos enviados via DTO e se eles forem nulos, podem passar sem tratamento de erros, pois numa atualização de entidade, podem existir campos nulos.
     * </h5>
     *
     * @param id 'ID' do cliente que será atualizado.
     * @param clienteUpdateRequestDTO DTO responsável pela exposição dos dados necessários para atualização de um cliente.
     * @param result Captura os erros de validação. Esses erros são verificados pela anotação @Valid do pacote Jakarta Validation.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200} ou {@link HttpStatus#BAD_REQUEST 400}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o cliente esteja excluído - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o cliente não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     *
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     * @see ErrorResponseDTO
     * @see HttpStatus
     * @see BindingResult
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Cliente está excluído"),
            @ApiResponse(responseCode = "400", description = "Campo fora do padrão")
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

    /**
     * <h2>
     *     Endpoint para ativar um cliente.
     * </h2>
     *
     * @param id ID do cliente que será ativado.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o cliente já esteja ativado - {@link HttpStatus#CONFLICT 409}.
     *
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see HttpStatus
     */
    @PatchMapping("/ativar/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente ativado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "Cliente não está excluído", useReturnTypeSchema = true)
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

    /**
     * <h2>
     *     Endpoint para listar o cliente dado um nome.
     * </h2>
     *
     * @param nome Nome do cliente que será buscado.
     * @return {@link ResponseEntity} com um List<{@link ClientePlanosResumoResponseDTO}> e Status Code {@link HttpStatus#OK 200}.
     *
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o cliente não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     *
     * @see ResponseEntity
     * @see ClientePlanosResumoResponseDTO
     * @see NotFoundException
     * @see HttpStatus
     */
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
