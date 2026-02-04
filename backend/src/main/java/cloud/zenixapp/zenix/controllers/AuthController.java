package cloud.zenixapp.zenix.controllers;

import cloud.zenixapp.zenix.dtos.LoginRequestDTO;
import cloud.zenixapp.zenix.dtos.ResponseDTO;
import cloud.zenixapp.zenix.dtos.RegisterRequestDTO;
import cloud.zenixapp.zenix.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.security.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints do serviço de Autenticação")
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body){
        Usuarios user = usuarioRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if(passwordEncoder.matches(body.senha(), user.getSenha())){
            String token = tokenService.generatedToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getNome(), token));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body){
        Optional<Usuarios> userByEmail = usuarioRepository.findByEmail(body.email());

        if (userByEmail.isEmpty()){
            Usuarios user = new Usuarios();
            user.setSenha(passwordEncoder.encode(body.senha()));
            user.setEmail(body.email());
            user.setNome(body.nome());
            user.setCpf(body.cpf());
            user.setGrupo(body.grupo());
            usuarioRepository.save(user);
            String token = tokenService.generatedToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getNome(), token));
        }

        return ResponseEntity.badRequest().build();
    }

}
