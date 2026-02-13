package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UsuarioExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.UsuarioMapper;
import cloud.zenixapp.zenix.models.dtos.SucessUsuarioResponseDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioLoginDTO;
import cloud.zenixapp.zenix.models.dtos.UsuarioResponseDTO;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.security.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        if (usuarioRepository.findByEmailEntities(user.email()) == -1){
            throw new UsuarioExcluidoException("Usuário foi excluído!");

        }
        var usernamePassword = new UsernamePasswordAuthenticationToken(user.email(), user.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        return tokenService.generateToken((Usuarios) auth.getPrincipal());
    }

    @Transactional
    public SucessUsuarioResponseDTO registerUser(UsuarioRequestDTO userRegister){
        if(usuarioRepository.findByEmail(userRegister.email()) != null){
            return null;

        }
        String encryptPassword = new BCryptPasswordEncoder().encode(userRegister.senha());
        UsuarioResponseDTO usuario = usuarioMapper.usuarioResponseDTO(
                usuarioRepository.save(new Usuarios(userRegister.nome(), userRegister.email(), encryptPassword, userRegister.cpf(), userRegister.grupo()))
        );

        return new SucessUsuarioResponseDTO(
            HttpStatus.CREATED.value(),
            "Usuário registrado com sucesso",
            usuario);
    }

    public List<UsuarioResponseDTO> buscarUsuarios(){
        return usuarioMapper.listResponseDTO(usuarioRepository.findAll());
    }

    @Transactional
    public SucessUsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO userDTO) throws NotFoundException {
        return usuarioRepository.findById(id)
                .map(user -> {
                    if(user.getStatus() == -1){
                        throw new NotFoundException("Usuário foi excluído!");

                    }

                    usuarioMapper.atualizarUsuario(user, userDTO);
                    if (userDTO.senha() != null && !userDTO.senha().isBlank()) {user.setSenha(new BCryptPasswordEncoder().encode(userDTO.senha()));}
                    UsuarioResponseDTO usuario = usuarioMapper.usuarioResponseDTO(usuarioRepository.save(user));

                    return new SucessUsuarioResponseDTO(
                        HttpStatus.OK.value(),
                        "Usuário atualizado com sucesso",
                        usuario
                    );

                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

    @Transactional
    public SucessUsuarioResponseDTO deletarUsuario(Long id){
        return usuarioRepository.findById(id)
                .map(user -> {
                    if(user.getStatus() == -1){
                        throw new UsuarioExcluidoException("Usuário já foi excluído!");

                    }

                    usuarioRepository.deleteLogico(id);
                    return new SucessUsuarioResponseDTO(
                        HttpStatus.OK.value(),
                        "Usuário deletado com sucesso",
                        null
                    );
                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

}
