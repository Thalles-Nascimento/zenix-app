package cloud.zenixapp.zenix.controllers;


import cloud.zenixapp.zenix.dtos.AuthenticationDTO;
import cloud.zenixapp.zenix.dtos.LoginTokenResponseDTO;
import cloud.zenixapp.zenix.dtos.UsuarioDTO;
import cloud.zenixapp.zenix.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO userLogin){
        var usernamePassword = new UsernamePasswordAuthenticationToken(userLogin.email(), userLogin.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuarios) auth.getPrincipal());

        return ResponseEntity.ok(new LoginTokenResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity registrer(@RequestBody @Valid UsuarioDTO usuarioDTO){
        if(usuarioRepository.findByEmail(usuarioDTO.email()) != null){
            return ResponseEntity.badRequest().build();
        }

        String encryptPassword = new BCryptPasswordEncoder().encode(usuarioDTO.senha());
        Usuarios user = new Usuarios(usuarioDTO.nome(), usuarioDTO.email(), encryptPassword, usuarioDTO.cpf(), usuarioDTO.grupo());

        return ResponseEntity.ok(usuarioRepository.save(user));

    }

}
