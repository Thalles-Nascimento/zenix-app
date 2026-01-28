package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.error.BindingHandler;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.dtos.AtendimentoUpdateRequestDTO;
import cloud.zenixapp.zenix.exceptions.AtendimentoException;
import cloud.zenixapp.zenix.services.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/atendimento")
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
    @Operation(summary = "Listar atendimentos", description = "Endpoint para listar todos os atendimentos")
    public ResponseEntity<List<AtendimentoResponseDTO>> findAll(){
        return ResponseEntity.ok().body(atendimentoService.listarAtendimentos());
    }

    /*
     * Endpoint para deletar um atendimento do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletar atendimento", description = "Endpoint para deletar um atendimento")
    public ResponseEntity<String> deleteAtendimento(@PathVariable Long id) throws AtendimentoException {
        if (atendimentoService.deletarAtendimento(id)){
            return ResponseEntity.ok().body("Atendimento Excluido");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possivel excluir!");

    }

    /*
     * Endpoint para buscar um atendimento do Banco de Dados pelo ID
     *
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "Listar atendimento por ID", description = "Endpoint para lista um atendimento por ID")
    public ResponseEntity<Optional<AtendimentoResponseDTO>> findById(@PathVariable Long id) {
        Optional<AtendimentoResponseDTO> atendimentoResponseDTO = atendimentoService.listarAtendimentoPorId(id);

        if (atendimentoResponseDTO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(atendimentoResponseDTO);
        }

        return ResponseEntity.ok().body(atendimentoResponseDTO);
    }

    /*
     * Endpoint para atualizar um atendimento do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @Operation(summary = "Atualizar atendimento por ID", description = "Endpoint para atualiza um atendimento por ID")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid AtendimentoRequestDTO atendimentoRequestDTO, BindingResult result) throws AtendimentoException {
//        System.out.println("Anotação do Erro: " + result.getFieldError().getCode() + "\nErro: " + result.getFieldError().getRejectedValue());

        if(result.hasErrors()){
            System.out.println(result.getFieldErrors());
            System.out.println(BindingHandler.updateError(result));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getFieldError().getDefaultMessage());
        }

        return ResponseEntity.ok().body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));

//            if(Objects.equals(result.getFieldError().getCode(), "Pattern")) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getFieldError().getDefaultMessage());
//            }
//            else if (Objects.equals(result.getFieldError().getCode(), "NotBlank") || Objects.equals(result.getFieldError().getCode(), "NotNull")){
//                Object campo = result.getFieldError().getRejectedValue();
//                if (campo == null){
//                    return ResponseEntity.ok().body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));
//
//                }
//                else{
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getFieldError().getDefaultMessage());
//
//                }
//            }



    }

}
