package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.ExistsException;
import cloud.zenixapp.zenix.models.dtos.requests.CadastroRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.CadastroResponseDTO;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroService {

    @Autowired
    private TenantRepository tenantsRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        // 3. Cria a Unidade vinculada ao Tenant
        Unidades unidade = new Unidades();
        unidade.setNomeUnidade(cadastroRequestDTO.nomeUnidade());
        unidade.setEndereco(cadastroRequestDTO.enderecoUnidade());
        unidade.setTenant(tenant);
        unidadeRepository.save(unidade);

        // 4. Cria o Usuário ADMIN
        Usuarios usuario = new Usuarios();
        usuario.setNome(cadastroRequestDTO.nomeAdmin());
        usuario.setEmail(cadastroRequestDTO.email());
        usuario.setSenha(passwordEncoder.encode(cadastroRequestDTO.senha()));
        usuario.setCpf(cadastroRequestDTO.cpf());
        usuario.setGrupo(UsuariosRoleEnum.ADMIN);
        usuario.setUnidade(unidade);
        usuario.setTenant(tenant);
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
