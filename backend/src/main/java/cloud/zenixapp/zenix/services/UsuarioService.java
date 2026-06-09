package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ExistsException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UsuarioExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.UsuarioMapper;
import cloud.zenixapp.zenix.models.dtos.requests.UsuarioLoginDTO;
import cloud.zenixapp.zenix.models.dtos.requests.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UsuarioResponseDTO;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.interfaces.UsuarioSimplesView;
import cloud.zenixapp.zenix.models.interfaces.UsuariosProjectionView;
import cloud.zenixapp.zenix.repositories.TenantRepository;
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

import java.time.LocalDateTime;
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
    private TenantRepository tenantRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private UnidadeService unidadeService;

    @Autowired
    private UnidadeRepository unidadeRepository;


    public Map<String, String> loginUser(@NonNull UsuarioLoginDTO user){
        if (usuarioRepository.existsByEmail(user.email())){
            if(usuarioRepository.findStatusByEmail(user.email()) == -1){
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

        else{
            throw new NotFoundException("Usuário não encontrado!");
        }


    }

    /*
    * Função para criar usuários - Barbeiros ou Administradores
    */
    @Transactional
    public SuccessResponseDTO criarNovoUsuario(UsuarioRequestDTO userRegister){
        if(usuarioRepository.existsByEmail(userRegister.email()) || usuarioRepository.existsByCpf(userRegister.cpf())){
            throw new ExistsException("Usuário já cadastrado!");

        }

        String tenantId = TenantContext.getTenantId();
        Tenants tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant não encontrado!"));

        String encryptPassword = new BCryptPasswordEncoder().encode(userRegister.senha());
        Usuarios newUser;

        if (userRegister.unidade() == null){
            newUser = new Usuarios(userRegister.nome(), userRegister.email(), null, encryptPassword, userRegister.cpf(), userRegister.grupo());

        }
        else{
            Unidades unidade = unidadeService.listarUnidadeByIdCompleto(userRegister.unidade(), tenantId);
            newUser = new Usuarios(userRegister.nome(), userRegister.email(), unidade, encryptPassword, userRegister.cpf(), userRegister.grupo());
        }

        newUser.setTenant(tenant);

        usuarioMapper.usuarioResponseDTO(usuarioRepository.save(newUser));

        return new SuccessResponseDTO(
            HttpStatus.CREATED.value(),
            "Usuário registrado com sucesso"
        );
    }

    /* Função que retorna todos os usuários via endpoint privado, necessária autenticação e passar o 'Tenant' via ‘token’*/
    public List<UsuariosProjectionView> buscarUsuarios(){
        return usuarioRepository.findAllByTenants(TenantContext.getTenantId());
    }

    /*
    * Função que retorna todos os usuários por unidade via endpoint público, não necessitando autenticação e passar o 'Tenant' via 'token'
    * Usado na tela de 'Login' da Fila
    */
    public List<UsuarioSimplesView> buscarBarbeirosPorUnidade(String unidadeId) {
        return usuarioRepository.findBarbeirosByUnidade(unidadeId);
    }

    /*
     * Função que atualiza um usuário
     */
//  Retry Pattern - lidar com indisponibilidade temporária
//  Dentro do Spring Boot o Retryable é Síncrono, ou seja, executa novamente na mesma Thread utilizando o mesmo Tenant
//    @Retryable(
//            includes = OptimisticLockException.class,
//            maxRetries = 3,
//            delay = 100,
//            multiplier = 2 // Evitar Retry storm - Backoff Exponential
//    )
    @Transactional
    public SuccessResponseDTO atualizarUsuario(String id, UsuarioRequestDTO userDTO) throws NotFoundException {
        String tenantId = TenantContext.getTenantId();
        return usuarioRepository.findById(id, tenantId)
                .map(user -> {
                    if (user.getStatus() == -1){
                        throw new UsuarioExcluidoException("Usuário foi excluído");

                    }

                    usuarioMapper.atualizarUsuario(user, userDTO);
                    if (userDTO.senha() != null && !userDTO.senha().isBlank()) {
                        user.setSenha(new BCryptPasswordEncoder().encode(userDTO.senha()));
                    }

                    if (userDTO.unidade() != null) {
//                        TODO Verificar o TenantId do Repository da Unidade
                        Unidades unidade = unidadeRepository.findById(userDTO.unidade())
                                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
                        user.setUnidade(unidade);
                    }

                    usuarioRepository.save(user);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Usuário atualizado com sucesso"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado ou excluído!"));
    }

    /*
     * Função que deleta um usuário
     */
    @Transactional
    public SuccessResponseDTO deletarUsuario(String id){
        String tenantId = TenantContext.getTenantId();
        return usuarioRepository.findById(id, tenantId)
                .map(user -> {
                    if (user.getStatus() == -1){
                        throw new UsuarioExcluidoException("Usuário já foi excluído");
                    }

                    user.setDeletedAt(LocalDateTime.now());
                    usuarioRepository.deleteLogico(id, tenantId);

                    return new SuccessResponseDTO(
                        HttpStatus.OK.value(),
                        "Usuário deletado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado ou excluído!"));
    }


    /*
     * Função que ativa um usuário
     */
    @Transactional
    public SuccessResponseDTO ativarUsuario(String id){
        String tenantId = TenantContext.getTenantId();
        return usuarioRepository.findById(id, tenantId)
                .map(user -> {
                    if (user.getStatus() != -1){
                        throw new UsuarioExcluidoException("Usuário está já ativado");
                    }
                    user.setDeletedAt(null);
                    usuarioRepository.ativarUsuario(id, tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Usuário ativado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

    public UsuarioResponseDTO getUsuarioID(){
        String tenantId = TenantContext.getTenantId();
        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuarios user = usuarioRepository.findById(userAuth.getId(), tenantId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));

        return usuarioMapper.usuarioResponseDTO(user);
    }

    public Usuarios getUsuarioById(String id){
        return usuarioRepository.findById(id, TenantContext.getTenantId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

}
