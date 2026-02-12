package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.UsuarioException;
import cloud.zenixapp.zenix.configs.mappers.UsuarioMapper;
import cloud.zenixapp.zenix.models.dtos.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioLoginDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioResponseDTO;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.security.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioMapper usuarioMapper;


    public String loginUser(UsuarioLoginDTO user){
        var usernamePassword = new UsernamePasswordAuthenticationToken(user.email(), user.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        return tokenService.generateToken((Usuarios) auth.getPrincipal());
    }

    @Transactional
    public UserDetails registerUser(UsuarioRequestDTO userRegister){
        if(usuarioRepository.findByEmail(userRegister.email()) != null){
            return null;
        }

        String encryptPassword = new BCryptPasswordEncoder().encode(userRegister.senha());
        Usuarios user = new Usuarios(userRegister.nome(), userRegister.email(), encryptPassword, userRegister.cpf(), userRegister.grupo());

        return usuarioRepository.save(user);
    }

    public List<UsuarioResponseDTO> buscarUsuarios(){
        return usuarioMapper.listResponseDTO(usuarioRepository.findAll());
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO userDTO) throws UsuarioException {
        return usuarioRepository.findById(id)
                .map(
                        user -> {
                            System.out.println("Usuário: " + user);
                            usuarioMapper.atualizarUsuario(user, userDTO);
                            if (userDTO.senha() != null && !userDTO.senha().isBlank()) {user.setSenha(new BCryptPasswordEncoder().encode(userDTO.senha()));}

                            return usuarioMapper.usuarioResponseDTO(usuarioRepository.save(user));
                        }
                ).orElseThrow(() -> new UsuarioException("Não foi possível encontrar o usuário"));
    }

}
