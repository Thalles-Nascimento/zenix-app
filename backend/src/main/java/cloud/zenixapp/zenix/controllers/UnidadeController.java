package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoAdminResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UnidadeResponseDTO;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.services.AtendimentoService;
import cloud.zenixapp.zenix.services.UnidadeService;
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
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/unidades")
@Tag(name = "Unidades", description = "Endpoints do serviço de Unidade")
public class UnidadeController {

    @Autowired
    private UnidadeService unidadeService;

    /*
    *Endpoint para inserção de uma unidade no Banco de Dados
    *
    */
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unidade inserido no banco"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Adicionar Unidade", description = "Endpoint para adiciona uma nova unidade")
    public ResponseEntity<?> save(@RequestBody @Valid UnidadeRequestDTO unidadeRequestDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(unidadeService.inserirUnidade(unidadeRequestDTO));
    }


    /*
    * Endpoint para buscar todas as unidades do Banco de Dados
    *
    */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade encontrada")
    })
    @Operation(summary = "Listar Unidades", description = "Endpoint para listar todas as unidades")
    public ResponseEntity<List<UnidadeResponseDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(unidadeService.listarUnidades());
    }


    /*
     * Endpoint para deletar uma unidade do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade excluída do banco"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    @Operation(summary = "Deletar unidade", description = "Endpoint para deletar uma unidade")
    public ResponseEntity<?> deleteAtendimento(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(unidadeService.deletarUnidade(id));

    }

    /*
     * Endpoint para buscar uma unidade do Banco de Dados pelo ID
     *
     */
    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    @Operation(summary = "Listar Unidade por ID", description = "Endpoint para lista uma unidade por ID")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(unidadeService.listarUnidadeById(id));
    }


    /*
     * Endpoint para atualizar uma unidade do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade atualizada"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Atualizar unidade por ID", description = "Endpoint para atualiza uma unidade por ID")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UnidadeRequestDTO unidadeRequestDTO, BindingResult result) throws NotFoundException {
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(unidadeService.atualizarUnidade(id, unidadeRequestDTO));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            "Alguns campos estão fora do padrão",
                            LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(unidadeService.atualizarUnidade(id, unidadeRequestDTO));

    }

}
