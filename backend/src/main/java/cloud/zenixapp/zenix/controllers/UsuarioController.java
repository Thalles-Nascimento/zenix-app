package cloud.zenixapp.zenix.controllers;


import cloud.zenixapp.zenix.configs.handlers.BindingHandler;
import cloud.zenixapp.zenix.models.dtos.requests.UsuarioLoginDTO;
import cloud.zenixapp.zenix.models.dtos.requests.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.*;
import cloud.zenixapp.zenix.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${api-url}/users")
@Tag(name = "Usuário", description = "Endpoints do serviço de Usuário")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Endpoint para realizar o login do usuário e obter um token de autenticação")
    public ResponseEntity<?> login(@RequestBody @Valid UsuarioLoginDTO userLogin, BindingResult result, HttpServletResponse response){
        if (result.hasErrors()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BindingHandler.insertError(result));

        Map<String, String> dados = usuarioService.loginUser(userLogin);

        // Controller aplica o cookie httpOnly na resposta — responsabilidade HTTP
        ResponseCookie cookie = ResponseCookie.from("auth_token", dados.get("token"))
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(2))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Remove o token do body — nunca deve ser exposto ao frontend
        dados.remove("token");

        return ResponseEntity.status(HttpStatus.OK)
                    .body(dados);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout de usuário", description = "Invalida o cookie de autenticação")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("auth_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/register")
    @Operation(summary = "Registro de usuário", description = "Endpoint para registrar um novo usuário no sistema")
    public ResponseEntity<?> registrer(@RequestBody @Valid UsuarioRequestDTO usuarioRequestDTO, BindingResult result){
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BindingHandler.insertError(result));
        }

        SucessUsuarioResponseDTO userRegister = usuarioService.registerUser(usuarioRequestDTO);

        if(userRegister == null){
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuário já cadastrado");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(error);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRegister);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Endpoint para listar todos os usuários cadastrados no sistema")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.buscarUsuarios());
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar usuário autenticado", description = "Endpoint para buscar os dados do usuário autenticado")
    public ResponseEntity<?> buscarMeUser(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.getUsuarioID());
    }

    @GetMapping("/barbeiros/{unidadeId}")
    @Operation(summary = "Listar barbeiros por unidade", description = "Endpoint público para listar barbeiros ativos de uma unidade")
    public ResponseEntity<List<UsuarioResponseSimplesDTO>> listarBarbeirosPorUnidade(@PathVariable Long unidadeId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.buscarBarbeirosPorUnidade(unidadeId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Endpoint para atualizar as informações de um usuário existente")
    public ResponseEntity<?> atualizarUsuarios(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDTO user, BindingResult result) {
        if (result.hasErrors()) {
            if (BindingHandler.isErrorNull(result)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(usuarioService.atualizarUsuario(id, user));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            "Alguns campos estão fora do padrão",
                            LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.atualizarUsuario(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Endpoint para excluir um usuário do sistema")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.deletarUsuario(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Ativar usuário", description = "Endpoint para ativar um usuário do sistema")
    public ResponseEntity<?> ativarUsuario(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.ativarUsuario(id));
    }

}
