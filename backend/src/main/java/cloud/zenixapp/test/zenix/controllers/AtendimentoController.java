package cloud.zenixapp.test.zenix.controllers;

import cloud.zenixapp.test.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.test.zenix.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.test.zenix.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.exceptions.AtendimentoException;
import cloud.zenixapp.test.zenix.services.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/atendimento")
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
    public ResponseEntity<AtendimentoResponseDTO> save(@RequestBody AtendimentoRequestDTO atendimentoDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.inserirAtendimento(atendimentoDTO));
    }

    /*
    * Endpoint para buscar todos os atendimentos do Banco de Dados
    *
    */
    @GetMapping
    @Operation(summary = "Listar atendimentos", description = "Endpoint para listar todos os atendimentos")
    public ResponseEntity<@NonNull List<AtendimentoResponseDTO>> findAll(){
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
    public ResponseEntity<Atendimento> update(@PathVariable Long id, @RequestBody AtendimentoRequestDTO atendimentoRequestDTO) throws AtendimentoException {
        return ResponseEntity.ok().body(atendimentoService.atualizarAtendimento(id, atendimentoRequestDTO));
    }

}
