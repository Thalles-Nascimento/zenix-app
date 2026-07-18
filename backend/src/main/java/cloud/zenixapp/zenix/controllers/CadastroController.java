package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.CadastroRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UnidadeResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UnidadeUserResponseDTO;
import cloud.zenixapp.zenix.services.CadastroService;
import cloud.zenixapp.zenix.services.UnidadeService;
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
@RequestMapping(value = "/${api-url}/cadastro")
@Tag(name = "Cadastros", description = "Endpoints para realizar Cadastro")
public class CadastroController {

    @Autowired
    private CadastroService cadastroService;

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
    public ResponseEntity<?> save(@RequestBody @Valid CadastroRequestDTO cadastroRequestDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cadastroService.cadastrar(cadastroRequestDTO));
    }




}
