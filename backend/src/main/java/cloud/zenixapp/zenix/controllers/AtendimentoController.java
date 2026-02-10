package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.error.BindingHandler;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.configs.exceptions.AtendimentoException;
import cloud.zenixapp.zenix.services.AtendimentoService;
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

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/atendimentos")
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
            @ApiResponse(responseCode = "422", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Adicionar atendimento", description = "Endpoint para adiciona um novo atendimento")
    public ResponseEntity<?> save(@RequestBody @Valid AtendimentoRequestDTO atendimentoDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.inserirAtendimento(atendimentoDTO));
    }

    /*
    * Endpoint para buscar todos os atendimentos do Banco de Dados
    *
    */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento encontrado")
    })
    @Operation(summary = "Listar atendimentos", description = "Endpoint para listar todos os atendimentos")
    public ResponseEntity<List<AtendimentoResponseDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(atendimentoService.listarAtendimentos());
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
    public ResponseEntity<?> deleteAtendimento(@PathVariable Long id) throws AtendimentoException {
        Map<String, String> dict = new HashMap<>();
        if (atendimentoService.deletarAtendimento(id)){
            dict.put("Message", "Atendimento Excluido!");
            return ResponseEntity.status(HttpStatus.OK).body(dict);
        }

        dict.put("Message", "Não foi possivel excluir!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dict);

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
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<AtendimentoResponseDTO> atendimentoResponseDTO = atendimentoService.listarAtendimentoPorId(id);

        if (atendimentoResponseDTO.isEmpty()){
            Map<String, String> empty = new HashMap<>();
            empty.put("Message", "Não encontramos o atendimento com esse ID!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(empty);
        }

        return ResponseEntity.status(HttpStatus.OK).body(atendimentoResponseDTO);
    }

    /*
     * Endpoint para atualizar um atendimento do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento atualizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Campos com nulos ou fora do padrão")
    })
    @Operation(summary = "Atualizar atendimento por ID", description = "Endpoint para atualiza um atendimento por ID")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid AtendimentoRequestDTO atendimentoRequestDTO, BindingResult result) throws AtendimentoException {
        if(result.hasErrors()){
            if (BindingHandler.updateError(result).isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BindingHandler.updateError(result));
        }

        return ResponseEntity.status(HttpStatus.OK).body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));

    }

}
