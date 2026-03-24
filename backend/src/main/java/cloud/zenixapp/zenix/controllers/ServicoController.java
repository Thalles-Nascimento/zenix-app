package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.mappers.ServicoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.ServicoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ServicoResponseDTO;
import cloud.zenixapp.zenix.services.ServicoService;
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
@RequestMapping(value = "/api/${api.version}/servicos")
@Tag(name = "Serviço", description = "Endpoints ddo Serviço")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private ServicoMapper servicoMapper;

    /*
    *Endpoint para inserção de um serviço no Banco de Dados
    *
    */
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço inserido no banco"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos")
    })
    @Operation(summary = "Adicionar serviço", description = "Endpoint para adiciona um novo serviço")
    public ResponseEntity<?> save(@RequestBody @Valid ServicoRequestDTO servicoRequestDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicoService.inserirServico(servicoRequestDTO));
    }

    /*
    * Endpoint para buscar todos os serviços do Banco de Dados
    *
    */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviços encontrado")
    })
    @Operation(summary = "Listar serviços", description = "Endpoint para listar todos os serviços")
    public ResponseEntity<List<ServicoResponseDTO>> findAllServicos(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(servicoService.buscarTodosServicos());
    }

    /*
     * Endpoint para deletar um serviço do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço excluído do banco"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @Operation(summary = "Deletar serviço", description = "Endpoint para deletar um serviço")
    public ResponseEntity<?> deleteServico(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(servicoService.deletarServico(id));

    }

    /*
     * Endpoint para buscar um serviço do Banco de Dados pelo ID
     *
     */
//    @GetMapping(value = "/{id}")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Atendimento encontrado"),
//            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
//    })
//    @Operation(summary = "Listar atendimento por ID", description = "Endpoint para lista um atendimento por ID")
//    public ResponseEntity<?> findById(@PathVariable Long id) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(atendimentoService.listarAtendimentoPorId(id));
//    }


    /*
     * Endpoint para atualizar um serviço do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
    })
    @Operation(summary = "Atualizar serviço por ID", description = "Endpoint para atualiza um serviço por ID")
    public ResponseEntity<?> updateServico(@PathVariable Long id, @RequestBody @Valid ServicoRequestDTO servicoRequestDTO, BindingResult result) throws NotFoundException {
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(servicoService.atualizarServico(servicoRequestDTO, id));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            "Alguns campos estão fora do padrão",
                            LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(servicoService.atualizarServico(servicoRequestDTO, id));

    }

}
