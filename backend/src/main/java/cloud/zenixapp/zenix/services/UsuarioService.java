package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.dtos.UsuarioDTO;
import cloud.zenixapp.zenix.dtos.UsuarioLoginDTO;
import cloud.zenixapp.zenix.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.security.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;


    public String loginUser(UsuarioLoginDTO user){
        var usernamePassword = new UsernamePasswordAuthenticationToken(user.email(), user.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        return tokenService.generateToken((Usuarios) auth.getPrincipal());
    }

    @Transactional
    public UserDetails registerUser(UsuarioDTO userRegister){
        if(usuarioRepository.findByEmail(userRegister.email()) != null){
            return null;
        }

        String encryptPassword = new BCryptPasswordEncoder().encode(userRegister.senha());
        Usuarios user = new Usuarios(userRegister.nome(), userRegister.email(), encryptPassword, userRegister.cpf(), userRegister.grupo());

        return usuarioRepository.save(user);
    }



}
