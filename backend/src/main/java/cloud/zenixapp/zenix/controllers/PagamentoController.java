package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.configs.mappers.PagamentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.PagamentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import cloud.zenixapp.zenix.models.interfaces.FormaPagamentoView;
import cloud.zenixapp.zenix.services.PagamentoService;
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
@RequestMapping(value = "/${api-url}/pagamentos")
@Tag(name = "Forma de Pagamento", description = "Endpoints de Forma de Pagamento")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PagamentoMapper pagamentoMapper;

    /*
    *Endpoint para inserção de um forma de pagamento no Banco de Dados
    *
    */
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Forma de pagamento inserida no banco"),
            @ApiResponse(responseCode = "400", description = "Campos com nulos")
    })
    @Operation(summary = "Adicionar forma de pagamento", description = "Endpoint para adiciona uma nova forma de pagamento")
    public ResponseEntity<?> save(@RequestBody @Valid PagamentoRequestDTO pagamentoRequestDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagamentoService.inserirPagamento(pagamentoRequestDTO));
    }

    /*
    * Endpoint para buscar todos as formas de pagamento do Banco de Dados
    *
    */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forma de pagamento encontrada")
    })
    @Operation(summary = "Listar formas de pagamento", description = "Endpoint para listar todos as formas de pagamento")
    public ResponseEntity<List<FormaPagamentoView>> findAllPagamentos(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagamentoService.buscarTodasFormaPagamento());
    }

    /*
     * Endpoint para deletar uma forma de pagamento do Banco de Dados pelo ID
     *
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forma de pagamento excluída do banco"),
            @ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada")
    })
    @Operation(summary = "Deletar forma de pagamento", description = "Endpoint para deletar uma forma de pagamento")
    public ResponseEntity<?> deletePagamento(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagamentoService.deletarPagamento(id));

    }

    /*
     * Endpoint para buscar uma forma de pagamento do Banco de Dados pelo ID
     *
     */
//    @GetMapping(value = "/{id}")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Atendimento encontrado"),
//            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
//    })
//    @Operation(summary = "Listar atendimento por ID", description = "Endpoint para lista um atendimento por ID")
//    public ResponseEntity<?> findById(@PathVariable String id) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(atendimentoService.listarAtendimentoPorId(id));
//    }


    /*
     * Endpoint para atualizar uma forma de pagamento do Banco de Dados pelo ID
     *
     */
    @PutMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forma de pagamento atualizada"),
            @ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada"),
    })
    @Operation(summary = "Atualizar forma de pagamento por ID", description = "Endpoint para atualiza uma forma de pagamento por ID")
    public ResponseEntity<?> updatePagamento(@PathVariable String id, @RequestBody @Valid PagamentoRequestDTO pagamentoRequestDTO, BindingResult result) throws NotFoundException {
        if(result.hasErrors()){
            if (BindingHandler.isErrorNull(result)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(pagamentoService.atualizarPagamento(pagamentoRequestDTO, id));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            "Alguns campos estão fora do padrão",
                            LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(pagamentoService.atualizarPagamento(pagamentoRequestDTO, id));

    }

}
