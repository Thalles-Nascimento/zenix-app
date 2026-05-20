package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoAdminResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.services.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@RequestMapping(value = "/${api-url}/atendimentos")
@Tag(name = "Atendimento", description = "Endpoints do serviço de Atendimento")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @Autowired
    private AtendimentoMapper atendimentoMapper;

    /*
    *Endpoint para inserção de um atendimento no Banco de Dados
    *
    */
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atendimento inserido no banco"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Adicionar atendimento", description = "Endpoint para adiciona um novo atendimento")
    public ResponseEntity<?> save(@RequestBody @Valid AtendimentoRequestDTO atendimentoDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.inserirAtendimento(atendimentoDTO));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<AtendimentoResponseDTO>> findHistorico(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarHistorico());
    }

    /*
    * Endpoint para buscar todos os atendimentos do Banco de Dados
    *
    */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento encontrado")
    })
    @Operation(summary = "Listar atendimentos do dia", description = "Endpoint para listar todos os atendimentos do dia")
    public ResponseEntity<List<AtendimentoResponseDTO>> findAllTodayByUser(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarAtendimentosHoje());
    }

//    TODO Verificar - @GetMapping("/admin")
    @GetMapping("/admin")
    @Operation(summary = "Listar todos os atendimentos", description = "Endpoint para ADMIN listar todos os atendimentos do dia")
    public ResponseEntity<List<AtendimentoAdminResponseDTO>> findAllAdmin(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarTodosAtendimentos());
    }

    /*
     * Endpoint para deletar um atendimento do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento excluído do banco"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    @Operation(summary = "Deletar atendimento", description = "Endpoint para deletar um atendimento")
    public ResponseEntity<?> deleteAtendimento(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.deletarAtendimento(id));

    }

    /*
     * Endpoint para buscar um atendimento do Banco de Dados pelo ID
     *
     */
    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    @Operation(summary = "Listar atendimento por ID", description = "Endpoint para lista um atendimento por ID")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.listarAtendimentoPorId(id));
    }

    /*
     * Endpoint para atualizar um atendimento do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento atualizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Atualizar atendimento por ID", description = "Endpoint para atualiza um atendimento por ID")
    public ResponseEntity<?> updateByAtendimento(@PathVariable String id, @RequestBody @Valid AtendimentoRequestDTO atendimentoRequestDTO, BindingResult result) throws NotFoundException {
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            "Alguns campos estão fora do padrão",
                            LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));

    }

    @PatchMapping("/{id}")
    @Operation(summary = "Ativar atendimento", description = "Endpoint para ativar um atendimento do sistema")
    public ResponseEntity<?> ativarAtendimento(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(atendimentoService.ativarAtendimento(id));
    }

}
