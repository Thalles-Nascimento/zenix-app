package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.mappers.PagamentoMapper;
import cloud.zenixapp.zenix.configs.mappers.PlanosMapper;
import cloud.zenixapp.zenix.models.dtos.requests.PagamentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.PlanosRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.PagamentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.PlanosResponseDTO;
import cloud.zenixapp.zenix.services.PagamentoService;
import cloud.zenixapp.zenix.services.PlanosService;
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
@RequestMapping(value = "/${api-url}/planos")
@Tag(name = "Planos", description = "Endpoints de Planos")
public class PlanoController {

    @Autowired
    private PlanosService planosService;

    @Autowired
    private PlanosMapper planosMapper;

    /*
    *Endpoint para inserção de um plano no Banco de Dados
    *
    */
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plano inserido no banco"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos")
    })
    @Operation(summary = "Adicionar plano", description = "Endpoint para adiciona um novo plano")
    public ResponseEntity<?> save(@RequestBody @Valid PlanosRequestDTO planosRequestDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planosService.inserirPlano(planosRequestDTO));
    }

    /*
    * Endpoint para buscar todos os planos do Banco de Dados
    *
    */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planos encontrados")
    })
    @Operation(summary = "Listar planos", description = "Endpoint para listar todos os planos")
    public ResponseEntity<List<PlanosResponseDTO>> findAllPlanos(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(planosService.buscarTodosPlanos());
    }

    /*
     * Endpoint para deletar um plano do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano excluído do banco"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado")
    })
    @Operation(summary = "Deletar plano", description = "Endpoint para deletar um plano")
    public ResponseEntity<?> deletePagamento(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planosService.deletarPlano(id));

    }

    /*
     * Endpoint para buscar um plano do Banco de Dados pelo ID
     *
     */
//    @GetMapping(value = "/{id}")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Plano encontrado"),
//            @ApiResponse(responseCode = "404", description = "Plano não encontrado")
//    })
//    @Operation(summary = "Listar plano por ID", description = "Endpoint para lista um plano por ID")
//    public ResponseEntity<?> findById(@PathVariable Long id) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(planoService.listarPlanoPorId(id));
//    }


    /*
     * Endpoint para atualizar um plano do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano atualizado"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado"),
    })
    @Operation(summary = "Atualizar plano por ID", description = "Endpoint para atualiza um plano por ID")
    public ResponseEntity<?> updatePlano(@PathVariable Long id, @RequestBody @Valid PlanosRequestDTO planosRequestDTO, BindingResult result) throws NotFoundException {
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(planosService.atualizarPlanos(planosRequestDTO, id));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            "Alguns campos estão fora do padrão",
                            LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(planosService.atualizarPlanos(planosRequestDTO, id));

    }

}
