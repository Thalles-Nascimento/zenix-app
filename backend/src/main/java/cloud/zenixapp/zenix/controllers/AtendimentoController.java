package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(value = "/${api-url}/atendimentos")
@Tag(name = "Atendimento", description = "Endpoints do serviço de Atendimento")
public class AtendimentoController {

    @Value("${api-url}")
    private String apiUrl;
    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    /*=====================================================================================
     * Endpoint para inserir um novo atendimento.
     *======================================================================================*/
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atendimento inserido no banco"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Adicionar atendimento", description = "Endpoint para adiciona um novo atendimento")
    public ResponseEntity<Object> save(@RequestBody @Valid AtendimentoRequestDTO atendimentoDTO, BindingResult result){
        log.info("[CONTROLLER] Classe e Método: AtendimentoController.save(Linha: 47) => Endpoint: {POST: /{}/atendimentos}", apiUrl);
        if (result.hasErrors()){
            Map<String, String> errors = BindingHandler.insertError(result);
            log.error("Corpo da requisição apresentando erros: [{}]", errors);
            log.info("[INSERIR ATENDIMENTO] Response -> {}", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }

        log.info("Inserindo atendimento...");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.inserirAtendimento(atendimentoDTO));
    }

    /*=====================================================================================
     * Endpoint para listar o histórico de atendimentos do usuário.
     *======================================================================================*/
    @GetMapping("/historico")
    public ResponseEntity<List<AtendimentoResponseDTO>> findHistorico(){
        log.info("[CONTROLLER] Classe e Método: AtendimentoController.findHistorico(Linha: 69) => Endpoint: {GET: /{}/atendimentos/historico}", apiUrl);
        log.info("Buscando o histórico de atendimentos do usuário...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarHistorico());
    }

    /*=====================================================================================
     * Endpoint para listar os atendimentos de hoje.
     *======================================================================================*/
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento encontrado")
    })
    @Operation(summary = "Listar atendimentos do dia", description = "Endpoint para listar todos os atendimentos do dia")
    public ResponseEntity<List<AtendimentoResponseDTO>> findAllTodayByUser(){
        log.info("[CONTROLLER] Classe e Método: AtendimentoController.findAllTodayByUser(Linha: 84) => Endpoint: {GET: /{}/atendimentos}", apiUrl);
        log.info("Buscando atendimentos do dia...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarAtendimentosHoje());
    }

    /*=====================================================================================
     * Endpoint para listar todos os atendimentos do usuário.
     *======================================================================================*/
    @GetMapping("/admin")
    @Operation(summary = "Listar todos os atendimentos", description = "Endpoint para ADMIN listar todos os atendimentos do dia")
    public ResponseEntity<List<AtendimentoResponseDTO>> findAllAdmin(){
        log.info("[CONTROLLER] Classe e Método: AtendimentoController.findAllAdmin(Linha: 96) => Endpoint: {GET: /{}/atendimentos/admin}", apiUrl);
        log.info("Buscando todos os atendimentos...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarTodosAtendimentos());
    }

    /*=====================================================================================
     * Endpoint para deletar um atendimento pelo ID.
     *======================================================================================*/
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento excluído do banco"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    @Operation(summary = "Deletar atendimento", description = "Endpoint para deletar um atendimento")
    public ResponseEntity<SuccessResponseDTO> deleteAtendimento(@PathVariable String id) {
        log.info("[CONTROLLER] Classe e Método: AtendimentoController.deleteAtendimento(Linha: 112) => Endpoint: {DELETE: /{}/atendimentos/[id]}", apiUrl);
        log.info("Deletando atendimento...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.deletarAtendimento(id));

    }


    /*=====================================================================================
     * Endpoint para atualizar um atendimento pelo ID.
     *======================================================================================*/
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento atualizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Atualizar atendimento por ID", description = "Endpoint para atualiza um atendimento por ID")
    public ResponseEntity<Object> updateByAtendimento(@PathVariable String id, @RequestBody @Valid AtendimentoRequestDTO atendimentoRequestDTO, BindingResult result) {
        log.info("[CONTROLLER] Classe e Método: AtendimentoController.updateByAtendimento(Linha: 131) => Endpoint: {PUT: /{}/atendimentos/[id]}", apiUrl);
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                log.info("Atualizando atendimento...");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));
            }
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Alguns campos estão fora do padrão",
                    LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant(ZoneOffset.of("-03:00"))
            );
            log.error("Erro ao tentar atualizar atendimento: [{}]", error);
            log.info("[ATUALIZAR ATENDIMENTO] Response -> {}", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }

        log.info("Atualizando atendimento...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));

    }

    /*=====================================================================================
     * Endpoint para ativar um atendimento pelo ID.
     *======================================================================================*/
    @PatchMapping("/{id}")
    @Operation(summary = "Ativar atendimento", description = "Endpoint para ativar um atendimento do sistema")
    public ResponseEntity<SuccessResponseDTO> ativarAtendimento(@PathVariable String id) {
        log.info("[CONTROLLER] Classe e Método: AtendimentoController.ativarAtendimento(Linha: 161) => Endpoint: {PATCH: /{}/atendimentos/[id]}", apiUrl);
        log.info("Ativando atendimento...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.ativarAtendimento(id));
    }

}
