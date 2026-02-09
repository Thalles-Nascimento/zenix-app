package cloud.zenixapp.zenix.controllers;


import cloud.zenixapp.zenix.dtos.UsuarioLoginDTO;
import cloud.zenixapp.zenix.dtos.LoginTokenResponseDTO;
import cloud.zenixapp.zenix.dtos.UsuarioDTO;
import cloud.zenixapp.zenix.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.UsuarioService;
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
@RequestMapping("/api/v1/users")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UsuarioLoginDTO userLogin){
        return ResponseEntity.ok(usuarioService.loginUser(userLogin));

    }

    @PostMapping("/register")
    public ResponseEntity registrer(@RequestBody @Valid UsuarioDTO usuarioDTO){
        if(usuarioService.registerUser(usuarioDTO) != null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(usuarioService.registerUser(usuarioDTO));

    }

}
