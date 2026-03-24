package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UsuarioExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.UsuarioJaExisteException;
import cloud.zenixapp.zenix.configs.mappers.UnidadeMapper;
import cloud.zenixapp.zenix.configs.mappers.UsuarioMapper;
import cloud.zenixapp.zenix.models.dtos.responses.SucessUsuarioResponseDTO;
import cloud.zenixapp.zenix.models.dtos.requests.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.UsuarioLoginDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UnidadeResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UsuarioResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UsuarioResponseSimplesDTO;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.security.TokenService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UnidadeService unidadeService;

    @Autowired
    private UnidadeRepository unidadeRepository;


    public Map<String, String> loginUser(@NonNull UsuarioLoginDTO user){
        if (usuarioRepository.querieStatusUser(user.email()) == -1){
            throw new UsuarioExcluidoException("Usuário foi excluído!");
        }

        Map<String, String> access = new HashMap<>();

        var usernamePassword = new UsernamePasswordAuthenticationToken(user.email(), user.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        Usuarios usuario = (Usuarios) auth.getPrincipal();
        access.put("token", tokenService.generateToken(usuario));
        access.put("nome", usuario.getNome());
        access.put("grupo", usuario.getGrupo().toString());

        return access;
    }

    public List<Usuarios> buscarUsuariosByUnidades(Long id){
        return usuarioRepository.findBarbeirosByUnidade(id);
    }

    @Transactional
    public SucessUsuarioResponseDTO registerUser(UsuarioRequestDTO userRegister){
        if(usuarioRepository.existsByEmail(userRegister.email()) || usuarioRepository.existsByCpf(userRegister.cpf())){
            throw new UsuarioJaExisteException("Usuário já cadastrado!");
        }
        String encryptPassword = new BCryptPasswordEncoder().encode(userRegister.senha());
        if (userRegister.unidade() == 0){
            UsuarioResponseDTO usuario = usuarioMapper.usuarioResponseDTO(
                    usuarioRepository
                            .save(new Usuarios(userRegister.nome(), userRegister.email(), encryptPassword, userRegister.cpf(), userRegister.grupo()))
            );
            return new SucessUsuarioResponseDTO(
                    HttpStatus.CREATED.value(),
                    "Usuário registrado com sucesso",
                    usuario);
        }
        Unidades unidade = unidadeService.listarUnidadeByIdCompleto(userRegister.unidade());

        UsuarioResponseDTO usuario = usuarioMapper.usuarioResponseDTO(
                usuarioRepository
                        .save(new Usuarios(userRegister.nome(), userRegister.email(), unidade, encryptPassword, userRegister.cpf(), userRegister.grupo()))
        );

        return new SucessUsuarioResponseDTO(
            HttpStatus.CREATED.value(),
            "Usuário registrado com sucesso",
            usuario);
    }

    public List<UsuarioResponseDTO> buscarUsuarios(){
        return usuarioMapper.listResponseDTO(usuarioRepository.findAll());
    }

    public List<UsuarioResponseSimplesDTO> buscarBarbeirosPorUnidade(Long unidadeId){
        return usuarioMapper.listResponseSimplesDTO(usuarioRepository.findBarbeirosByUnidade(unidadeId));
    }

    @Transactional
    public SucessUsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO userDTO) throws NotFoundException {
        return usuarioRepository.findById(id)
                .map(user -> {
                    if(user.getStatus() == -1){
                        throw new NotFoundException("Usuário foi excluído!");
                    }

                    usuarioMapper.atualizarUsuario(user, userDTO);
                    if (userDTO.senha() != null && !userDTO.senha().isBlank()) {
                        user.setSenha(new BCryptPasswordEncoder().encode(userDTO.senha()));
                    }

                    if (userDTO.unidade() != null) {
                        Unidades unidade = unidadeRepository.findById(userDTO.unidade())
                                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
                        user.setUnidade(unidade);
                    }

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

    @Transactional
    public SucessUsuarioResponseDTO ativarUsuario(Long id){
        return usuarioRepository.findById(id)
                .map(user -> {
                    if(user.getStatus() != -1){
                        throw new UsuarioExcluidoException("Usuário já está ativo!");
                    }
                    usuarioRepository.ativarUsuario(id);
                    return new SucessUsuarioResponseDTO(
                            HttpStatus.OK.value(),
                            "Usuário ativado com sucesso",
                            usuarioMapper.usuarioResponseDTO(user)
                    );
                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

    public UsuarioResponseDTO getUsuarioID(){
        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioRepository.findById(userAuth.getId())
                .map(user -> {
                    if(user.getStatus() == -1){
                        throw new UsuarioExcluidoException("Usuário foi excluído!");
                    }
                    return usuarioMapper.usuarioResponseDTO(user);
                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

    public Usuarios getUsuarioById(Long id){
        return usuarioRepository.findById(id)
                .map(user -> {
                    if(user.getStatus() == -1){
                        throw new UsuarioExcluidoException("Usuário foi excluído!");
                    }
                    return user;
                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

}
