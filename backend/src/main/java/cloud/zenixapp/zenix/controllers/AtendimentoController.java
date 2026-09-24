package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.ConflictException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.utils.HelpersLogs;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.services.AtendimentoService;
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
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static cloud.zenixapp.zenix.configs.utils.HelpersVar.CAMADA_CONTROLLER;
import static cloud.zenixapp.zenix.configs.utils.HelpersVar.TIME_ZONE;

/**
 * <h2>
 *     Controlador de requisições HTTP para o Domínio Atendimento, responsável por expor os endpoints deste.
 * </h2>
 * <p>
 *     Este componente lida com as requisições HTTP mapeadas sob o caminho {@code /api/v2/atendimentos}
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
@RequestMapping(value = "/${api-url}/atendimentos")
@Tag(name = "Atendimentos", description = "Endpoints do Domínio Atendimento")
public class AtendimentoController {

    @Value("${api-url}")
    public String apiUrl;

    // Classe padrão para 'log'
    private static final String CLASS_NAME = "AtendimentoController";

    // Entidade padrão para 'log'
    private static final String ENTITY_NAME = "Atendimento";

    // Resposta padrão para 'log' de atendimentos encontrados
    private static final String ATENDIMENTO_FOUND = "Atendimentos encontrados";

    private final AtendimentoService atendimentoService;

    /**
     * <h2>
     *     Construtor padrão para injeção de dependência automatizada pelo Spring.
     * </h2>
     *
     * @param atendimentoService É o serviço que contêm as regras de negócios do Domínio Atendimento.
     * @see AtendimentoService
     */
    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    /**
     * <h2>
     *     Endpoint para inserir um novo atendimento no sistema.
     * </h2>
     *
     * @param atendimentoDTO DTO responsável pela exposição dos dados necessários para inserção do atendimento.
     * @param result Captura os erros de validação. Esses erros são verificados pela anotação @Valid do pacote Jakarta Validation
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 201} e {@link HttpStatus#BAD_REQUEST 400}.
     * @see ResponseEntity
     * @see SuccessResponseDTO
     * @see HttpStatus
     * @see BindingResult
     */
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Para atendimentos inseridos com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Adicionar Atendimento", description = "Endpoint para adiciona um novo atendimento")
    public ResponseEntity<Object> save(@RequestBody @Valid AtendimentoRequestDTO atendimentoDTO, BindingResult result){
        HelpersLogs.logInfoController("POST", CLASS_NAME, "save", apiUrl);

        if (result.hasErrors()){
            Map<String, String> errors = BindingHandler.insertError(result);
            HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.BAD_REQUEST, "Não foi possível inserir o atendimento.");
            log.debug("AtendimentoController.save => Erros = [{}]", errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);

        }

        log.debug("Inserindo atendimento...");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.inserirAtendimento(atendimentoDTO));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.CREATED, "Atendimento inserido.");
        return response;

    }

    /**
     * <h2>
     *     Endpoint para listar o histórico de atendimentos.
     * </h2>
     * <p>
     *     No service é passado o 'ID' do usuário para a query no banco que realizou a requisição.
     * </p>
     * @return {@link ResponseEntity} com um List<{@link AtendimentoResponseDTO}> e Status Code {@link HttpStatus#OK 200}.
     * Pode retornar uma lista vazia caso não tenham atendimentos no sistema.
     * @see ResponseEntity
     * @see AtendimentoResponseDTO
     * @see HttpStatus
     */
    @GetMapping("/historico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ATENDIMENTO_FOUND, useReturnTypeSchema = true)
    })
    @Operation(summary = "Listar o histórico de atendimentos", description = "Endpoint para listar todos os atendimentos feitos pelo barbeiro")
    public ResponseEntity<List<AtendimentoResponseDTO>> findHistorico(){
        HelpersLogs.logInfoController("GET(/historico)", CLASS_NAME, "findHistorico", apiUrl);

        log.debug("Buscando o histórico de atendimentos do usuário...");
        ResponseEntity<List<AtendimentoResponseDTO>> response = ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarHistorico());

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, ATENDIMENTO_FOUND);
        return response;
    }

    /**
     * <h2>
     *     Endpoint para listar os atendimentos do dia.
     * </h2>
     * <p>
     *     No service é passado o 'ID' do usuário para a query no banco que realizou a requisição.
     * </p>
     * @return {@link ResponseEntity} com um List<{@link AtendimentoResponseDTO}> e Status Code {@link HttpStatus#OK 200}. Pode retornar uma lista vazia caso não tenham atendimentos no sistema.
     * @see ResponseEntity
     * @see AtendimentoResponseDTO
     * @see HttpStatus
     */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ATENDIMENTO_FOUND, useReturnTypeSchema = true)
    })
    @Operation(summary = "Listar atendimentos do dia", description = "Endpoint para listar todos os atendimentos do dia")
    public ResponseEntity<List<AtendimentoResponseDTO>> findAllTodayByUser(){
        HelpersLogs.logInfoController("GET", CLASS_NAME, "findAllTodayByUser", apiUrl);

        log.debug("Buscando atendimentos do dia...");
        ResponseEntity<List<AtendimentoResponseDTO>> response = ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarAtendimentosHojeByUsuario());

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, ATENDIMENTO_FOUND);

        return response;
    }

    /**
     * <h2>
     *     Endpoint para o 'Usuário' de nível Administrador poder listar todos os atendimentos da sua barbearia
     * </h2>
     *
     * @return {@link ResponseEntity} com um List<{@link AtendimentoResponseDTO}> e Status Code {@link HttpStatus#OK 200}. Pode retornar uma lista vazia caso não tenham atendimentos no sistema.
     * @see ResponseEntity
     * @see AtendimentoResponseDTO
     * @see HttpStatus
     */
    @GetMapping("/admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ATENDIMENTO_FOUND, useReturnTypeSchema = true)
    })
    @Operation(summary = "Listar todos os atendimentos by Administrador", description = "Endpoint para listar todos os atendimentos => Administrador")
    public ResponseEntity<List<AtendimentoResponseDTO>> findAllAdmin(){
        HelpersLogs.logInfoController("GET(/admin)", CLASS_NAME, "findAllAdmin", apiUrl);

        log.debug("Buscando todos os atendimentos...");
        ResponseEntity<List<AtendimentoResponseDTO>> response = ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarTodosAtendimentos());

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, ATENDIMENTO_FOUND);

        return response;
    }

    /**
     * <h2>
     *     Endpoint para deletar um atendimento.
     * </h2>
     * <p>
     *     O atendimento é deletado logicamente via coluna {@link Atendimento#getStatus() Status}.
     * </p>
     * @param id ID do atendimento que será excluído.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o atendimento já esteja excluído - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o atendimento não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     * @see HttpStatus
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento excluído do banco", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "Atendimento já está excluído", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado", useReturnTypeSchema = true)
    })
    @Operation(summary = "Deletar atendimento", description = "Endpoint para deletar um atendimento")
    public ResponseEntity<SuccessResponseDTO> deleteAtendimento(@PathVariable String id) {
        HelpersLogs.logInfoController("DELETE(/id)", CLASS_NAME, "deleteAtendimento", apiUrl);

        log.debug("Deletando atendimento...");
        ResponseEntity<SuccessResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.deletarAtendimento(id));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Atendimento deletado.");

        return response;

    }


    /**
     * <h2>
     *     Endpoint para atualizar um atendimento.
     * </h2>
     *
     * <h5>
     *     Obs: há uma validação para os campos enviados via DTO e que eles forem nulos, podem passar sem tratamento de erros, pois numa atualização de entidade, podem existir campos nulos.
     * </h5>
     *
     * @param id 'ID' do atendimento que será atualizado.
     * @param atendimentoRequestDTO DTO responsável pela exposição dos dados necessários para atualização de um atendimento.
     * @param result Captura os erros de validação. Esses erros são verificados pela anotação @Valid do pacote Jakarta Validation.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200} e {@link HttpStatus#BAD_REQUEST 400} com {@link ErrorResponseDTO}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o atendimento esteja excluído - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o atendimento não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     * @see ErrorResponseDTO
     * @see HttpStatus
     * @see BindingResult
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento atualizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Atendimento está excluído"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Atualizar atendimento por ID", description = "Endpoint para atualiza um atendimento por ID")
    public ResponseEntity<Object> updateByAtendimento(@PathVariable String id, @RequestBody @Valid AtendimentoRequestDTO atendimentoRequestDTO, BindingResult result) {
        HelpersLogs.logInfoController("PUT(/id)", CLASS_NAME, "updateByAtendimento", apiUrl);

        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                log.debug("Atualizando atendimento...");
                ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                        .body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));

                HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Atendimento atualizado.");
                return response;

            }
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Alguns campos estão fora do padrão",
                    LocalDateTime.now(TIME_ZONE).toInstant(ZoneOffset.of("-03:00"))
            );

            HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.BAD_REQUEST, "Não foi possível atualizar o atendimento.");
            log.debug("AtendimentoController.updateByAtendimento => Erros = [{}]", error);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }

        log.debug("Atualizando atendimento...");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Atendimento atualizado.");
        return response;

    }

    /**
     * <h2>
     *     Endpoint para ativar um atendimento excluído.
     * </h2>
     *
     * @param id 'ID' do atendimento que será ativado.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o atendimento já esteja ativado - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o atendimento não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     * @see HttpStatus
     */
    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento ativado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Atendimento não está excluído")
    })
    @Operation(summary = "Ativar atendimento", description = "Endpoint para ativar um atendimento do sistema")
    public ResponseEntity<SuccessResponseDTO> ativarAtendimento(@PathVariable String id) {
        HelpersLogs.logInfoController("PATCH(/id)", CLASS_NAME, "ativarAtendimento", apiUrl);

        log.debug("Ativando atendimento...");
        ResponseEntity<SuccessResponseDTO> response = ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.ativarAtendimento(id));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.OK, "Atendimento ativado.");

        return response;
    }

}
