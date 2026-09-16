package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.ExistsException;
import cloud.zenixapp.zenix.models.dtos.requests.CadastroRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.usuarios.CadastroResponseDTO;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h2>
 *     Serviço que estabelece as regras de negócio para cadastrar uma conta no sistema.
 * </h2>
 * Métodos:
 * {@link CadastroService#cadastrar(CadastroRequestDTO) Cadastrar}
 * {@link CadastroService#gerarSlug(String) Gerar Slug}
 *
 * @version 1.0
 * @author Thalles Nascimento
 */
@Service
public class CadastroService {

    private final TenantRepository tenantsRepository;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * <h2>
     *     Construtor padrão da classe CadastroService para automatizar a injeção de dependência gerenciada pelo Spring.
     * </h2>
     *
     * @param tenantsRepository Repositório do domínio tenant utilizado para realizar consultas no banco de dados.
     * @param unidadeRepository Repositório do domínio unidade utilizado para realizar consultas no banco de dados.
     * @param usuarioRepository Repositório do domínio usuário utilizado para realizar consultas no banco de dados.
     * @param passwordEncoder Classe responsável pela criptografia da senha.
     * @see TenantRepository
     * @see UnidadeRepository
     * @see UsuarioRepository
     * @see PasswordEncoder
     * @see Tenants
     */
    public CadastroService(TenantRepository tenantsRepository, UnidadeRepository unidadeRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.tenantsRepository = tenantsRepository;
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public CadastroResponseDTO cadastrar(CadastroRequestDTO cadastroRequestDTO) {

        // 1. Validações
        if (usuarioRepository.existsByEmail(cadastroRequestDTO.email())) {
            throw new ExistsException("E-mail já cadastrado!");
        }
        if (usuarioRepository.existsByCpf(cadastroRequestDTO.cpf())) {
            throw new ExistsException("CPF já cadastrado!");
        }
        if (tenantsRepository.existsByNome(cadastroRequestDTO.nomeEmpresa()) || tenantsRepository.existsByCnpj(cadastroRequestDTO.cnpj())) {
            throw new ExistsException("Empresa já cadastrada");
        }

        // 2. Cria o Tenant
        Tenants tenant = new Tenants();
        tenant.setNome(cadastroRequestDTO.nomeEmpresa());
        tenant.setSlug(gerarSlug(cadastroRequestDTO.nomeEmpresa()));
        tenant.setCnpj(cadastroRequestDTO.cnpj());
        tenantsRepository.save(tenant);

        String tenantId = tenantsRepository.findIdByCnpj(cadastroRequestDTO.cnpj());
        // 3. Cria a Unidade vinculada ao Tenant
        Unidades unidade = new Unidades();
        unidade.setNomeUnidade(cadastroRequestDTO.nomeUnidade());
        unidade.setEndereco(cadastroRequestDTO.enderecoUnidade());
        unidade.setTenant(tenantId);
        unidadeRepository.save(unidade);

        // 4. Cria o Usuário ADMIN
        Usuarios usuario = new Usuarios();
        usuario.setNome(cadastroRequestDTO.nomeAdmin());
        usuario.setEmail(cadastroRequestDTO.email());
        usuario.setSenha(passwordEncoder.encode(cadastroRequestDTO.senha()));
        usuario.setCpf(cadastroRequestDTO.cpf());
        usuario.setGrupo(UsuariosRoleEnum.ADMIN);
        usuario.setUnidade(unidade);
        usuario.setTenant(tenantId);
        usuarioRepository.save(usuario);

        return new CadastroResponseDTO(
                "Cadastro realizado com sucesso!",
                tenant.getNome(),
                usuario.getEmail()
        );

    }

    private String gerarSlug(String nome) {
        return nome.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

}
