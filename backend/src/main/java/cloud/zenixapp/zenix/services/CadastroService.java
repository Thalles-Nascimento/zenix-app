package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.ConflictException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.models.dtos.requests.CadastroRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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
@Log4j2
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


    /**
     * <h2>
     *     Método para cadastro.
     * </h2>
     * <p>
     *     Este método cadastra um novo Cliente - barbearia - no sistema.
     * </p>
     * @param cadastroRequestDTO DTO responsável pela exposição dos dados necessários para cadastra-se no sistema.
     * @return {@link SuccessResponseDTO}
     * @see Transactional
     * @see CadastroRequestDTO
     */
    @Transactional
    public SuccessResponseDTO cadastrar(CadastroRequestDTO cadastroRequestDTO) {
        log.info("[SERVICE -> Cadastrar] : CadastroService.cadastrar(Linha: 62)");

        // 1. Validações
        if (usuarioRepository.existsByEmail(cadastroRequestDTO.email())) {
            log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.CadastroService.cadastrar(CadastroRequestDTO)");
            throw new ConflictException("[Verificação de E-mail] E-mail já cadastrado!");
        }
        if (usuarioRepository.existsByCpf(cadastroRequestDTO.cpf())) {
            log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.CadastroService.cadastrar(CadastroRequestDTO)");
            throw new ConflictException("[Verificação de CPF] CPF já cadastrado!");
        }
        if (tenantsRepository.existsByNome(cadastroRequestDTO.nomeEmpresa()) || tenantsRepository.existsByCnpj(cadastroRequestDTO.cnpj())) {
            log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.CadastroService.cadastrar(CadastroRequestDTO)");
            throw new ConflictException("[Verificação de Empresa] Empresa já cadastrada");
        }

        // 2. Cria o Tenant
        log.info("Criando o Tenant...");
        Tenants tenant = new Tenants();
        tenant.setNome(cadastroRequestDTO.nomeEmpresa());
        tenant.setSlug(gerarSlug(cadastroRequestDTO.nomeEmpresa()));
        tenant.setCnpj(cadastroRequestDTO.cnpj());
        tenantsRepository.save(tenant);
        log.info("Tenant cadastrado!");

        log.info("Buscando o tenantId...");
        String tenantId = tenantsRepository.findIdByCnpj(cadastroRequestDTO.cnpj());
        if (tenantId == null){
            log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.CadastroService.cadastrar(CadastroRequestDTO)");
            throw new NotFoundException("[Consulta TenantID] Não foi encontrada empresa com esse CNPJ.");
        }
        log.info("Empresa encontrada!");

        // 3. Cria a Unidade vinculada ao Tenant
        log.info("Criando Unidade...");
        Unidades unidade = new Unidades();
        unidade.setNomeUnidade(cadastroRequestDTO.nomeUnidade());
        unidade.setEndereco(cadastroRequestDTO.enderecoUnidade());
        unidade.setTenant(tenantId);
        unidadeRepository.save(unidade);
        log.info("Unidade cadastrada!");

        // 4. Cria o Usuário ADMIN
        log.info("Criando Usuário...");
        Usuarios usuario = new Usuarios();
        usuario.setNome(cadastroRequestDTO.nomeAdmin());
        usuario.setEmail(cadastroRequestDTO.email());
        usuario.setSenha(passwordEncoder.encode(cadastroRequestDTO.senha()));
        usuario.setCpf(cadastroRequestDTO.cpf());
        usuario.setGrupo(UsuariosRoleEnum.ADMIN);
        usuario.setUnidade(unidade);
        usuario.setTenant(tenantId);
        usuarioRepository.save(usuario);
        log.info("Usuário cadastrado!");

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Cadastro realizado!"
        );

        log.info("Response: [Status: {}] => [Message: {}]", response.status(), response.message());

        return response;

    }

    /**
     * <h2>
     *     Método auxiliar para criar o Slug.
     * </h2>
     * <p>
     *     Este método é responsável pela criação do Slug a partir do nome da empresa/Barbearia.
     * </p>
     *
     * @param nome Nome da empresa/Barbearia
     * @return {@code slugName}
     */
    private String gerarSlug(String nome) {
        log.info("Gerando Slug de '{}'...", nome);
        String slugName = nome.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");

        log.info("Slug criado! [Slug = '{}']", slugName);
        return slugName;
    }

}
