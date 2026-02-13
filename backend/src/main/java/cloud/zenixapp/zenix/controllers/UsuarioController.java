package cloud.zenixapp.zenix.controllers;


import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.SucessUsuarioResponseDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioLoginDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioResponseDTO;
import cloud.zenixapp.zenix.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuário", description = "Endpoints do serviço de Usuário")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Endpoint para realizar o login do usuário e obter um token de autenticação")
    public ResponseEntity<?> login(@RequestBody @Valid UsuarioLoginDTO userLogin, BindingResult result){
        if (result.hasErrors()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BindingHandler.insertError(result));

        Map<String, String> token = new HashMap<>();
        token.put("token", usuarioService.loginUser(userLogin));

        return ResponseEntity.status(HttpStatus.OK).body(token);

    }

    @PostMapping("/register")
    @Operation(summary = "Registro de usuário", description = "Endpoint para registrar um novo usuário no sistema")
    public ResponseEntity<?> registrer(@RequestBody @Valid UsuarioRequestDTO usuarioRequestDTO, BindingResult result){
        if (result.hasErrors()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BindingHandler.insertError(result));

        SucessUsuarioResponseDTO userRegister = usuarioService.registerUser(usuarioRequestDTO);

        if(userRegister == null){
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuário já cadastrado");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userRegister);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Endpoint para listar todos os usuários cadastrados no sistema")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios(){
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUsuarios());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Endpoint para atualizar as informações de um usuário existente")
    public ResponseEntity<?> atualizarUsuarios(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDTO user, BindingResult result) throws NotFoundException {
        if (result.hasErrors()) {
            Map<String, String> erros = BindingHandler.updateError(result);
            if (erros.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(usuarioService.atualizarUsuario(id, user));

            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
        }

        SucessUsuarioResponseDTO usuario = usuarioService.atualizarUsuario(id, user);
        if (usuario == null){
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

}
