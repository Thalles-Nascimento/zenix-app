package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.ConflictException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.utils.HelpersLogs;
import cloud.zenixapp.zenix.models.dtos.requests.CadastroRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.services.CadastroService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static cloud.zenixapp.zenix.configs.utils.HelpersVar.CAMADA_CONTROLLER;

/**
 * <h2>
 *     Controlador de requisições HTTP para o cadastro de uma barbearia.
 * </h2>
 * <p>
 *     Este componente lida com as requisições HTTP mapeadas sob o caminho {@code /api/v2/cadastro}
 * </p>
 *
 * Métodos HTTP suportados pelo controller:
 * {@code POST}

 * @version 1.0
 * @author Thalles Nascimento
 *
 */
@Log4j2
@RestController
@RequestMapping(value = "/${api-url}/cadastro")
@Tag(name = "Cadastros", description = "Endpoints para realizar Cadastro")
public class CadastroController {

    @Value("${api-url}")
    private String apiUrl;

    // Classe padrão para 'log'
    private static final String CLASS_NAME = "CadastroController";

    // Entidade padrão para 'log'
    private static final String ENTITY_NAME = "Cadastro";

    private final CadastroService cadastroService;

    /**
     * <h2>
     *     Construtor padrão para injeção de dependência automatizada pelo Spring.
     * </h2>
     * @param cadastroService Serviço responsável pelas regras de negócio do cadastro.
     * @see CadastroService
     */
    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }


    /**
     * <h2>
     *     Endpoint para inserir um novo cadastro no sistema.
     * </h2>
     * @param cadastroRequestDTO DTO responsável pela exposição dos dados necessários para cadastra-se no sistema.
     * @param result Captura os erros de validação. Esses erros são verificados pela anotação @Valid do pacote Jakarta Validation.
     * @return {@link ResponseEntity} com o {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 201} e {@link HttpStatus#BAD_REQUEST 400}.
     * @throws ConflictException Caso haja conflito no cadastro - {@link HttpStatus#CONFLICT 409}.
     * @throws NotFoundException Caso o Tenant não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     * @see ResponseEntity
     * @see SuccessResponseDTO
     * @see HttpStatus
     * @see BindingResult
     */
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cadastro realizado."),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão."),
            @ApiResponse(responseCode = "404", description = "Tenant não encontrado."),
            @ApiResponse(responseCode = "409", description = "Conflito no cadastro.")
    })
    @Operation(summary = "Cadastrar uma Barbearia", description = "Endpoint para cadastrar uma nova Barbearia")
    public ResponseEntity<Object> save(@RequestBody @Valid CadastroRequestDTO cadastroRequestDTO, BindingResult result){
        HelpersLogs.logInfoController(HttpMethod.POST.name(), CLASS_NAME, "save", apiUrl);

        if (result.hasErrors()){
            Map<String, String> errors = BindingHandler.insertError(result);
            HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.BAD_REQUEST, "Não foi possível realizar o cadastro.");
            log.debug("{}.save => Erros = [{}]", CLASS_NAME, errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);

        }

        log.debug("Cadastrando Barbearia...");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED)
                .body(cadastroService.cadastrar(cadastroRequestDTO));

        HelpersLogs.logResponse(CAMADA_CONTROLLER, ENTITY_NAME, HttpStatus.CREATED, "Cadastro realizado.");
        return response;

    }

}
