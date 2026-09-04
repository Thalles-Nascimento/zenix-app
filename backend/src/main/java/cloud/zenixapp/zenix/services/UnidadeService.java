package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ExistsException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UnidadeAtivaException;
import cloud.zenixapp.zenix.configs.exceptions.UnidadeExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.UnidadeMapper;
import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UnidadeUserResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UsuarioSimplesResponseDTO;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import cloud.zenixapp.zenix.models.interfaces.UnidadeSimplesView;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UnidadeService {

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private UnidadeMapper unidadeMapper;

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public SuccessResponseDTO inserirUnidade(UnidadeRequestDTO unidadeDTO){
        String tenantId = TenantContext.getTenantId();

        if (unidadeRepository.existsByNomeUnidadeAndTenant(unidadeDTO.nomeUnidade(), tenantId)){
            throw new ExistsException("Unidade " + unidadeDTO.nomeUnidade() + " já cadastrada");
        }

        Unidades unidade = unidadeMapper.toUnidade(unidadeDTO);
        unidade.setTenant(tenantId);

        unidadeRepository.save(unidade);

        return new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Unidade inserida com sucesso"
        );
    }

    public List<UnidadeSimplesView> listarUnidades(){
        return unidadeRepository.findUnidadesByTenant(TenantContext.getTenantId());
    }

    public UnidadeSimplesView listarUnidadeById(String id){
        return unidadeRepository.findById(id, TenantContext.getTenantId())
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade foi excluída!");

                    }

                    return unidade;

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

//  TODO ajustar o método abaixo
    public UnidadeUserResponseDTO listarUnidadesByIdUsuario(String id){
        Unidades unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));

        List<UsuarioSimplesResponseDTO> usuarios = unidade.getUsuarios()
                .stream()
                .filter(u -> u.getGrupo() == UsuariosRoleEnum.USER)
                .map(u -> new UsuarioSimplesResponseDTO(
                        u.getId(),
                        u.getNome(),
                        u.getEmail(),
                        u.getGrupo(),
                        u.getStatus()
                ))
                .toList();

        return new UnidadeUserResponseDTO(
                unidade.getId(),
                unidade.getNomeUnidade(),
                unidade.getEndereco(),
                unidade.getStatus(),
                usuarios
        );
    }

    public Unidades listarUnidadeByIdCompleto(String id, String tenantId){
        return unidadeRepository.findById(id, tenantId)
                .map(unidadeView -> {
                    if(unidadeView.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade foi excluída!");

                    }

                    return unidadeMapper.fromUnidadesViewToUnidades(unidadeView);

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    @Transactional
    public SuccessResponseDTO atualizarUnidade(String id, UnidadeRequestDTO unidadeDTO){
        return unidadeRepository.findById(id, TenantContext.getTenantId())
                .map(unidadeView -> {
                    if(unidadeView.getStatus() == -1){
                        throw new NotFoundException("Unidade foi excluída!");

                    }

                    Unidades unidade = unidadeMapper.fromUnidadesViewToUnidades(unidadeView);

                    unidadeMapper.atualizarUnidade(unidade, unidadeDTO);
                    unidadeRepository.save(unidade);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade atualizada com sucesso"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

//  TODO ajustar o método abaixo
    @Transactional
    public SuccessResponseDTO deletarUnidade(String id){
        String tenantId = TenantContext.getTenantId();
        return unidadeRepository.findById(id, tenantId)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade já foi excluída!");

                    }

                    Unidades unidadeEntity = unidadeMapper.fromUnidadesViewToUnidades(unidade);
                    unidadeEntity.setDeletedAt(LocalDateTime.now());
                    unidadeRepository.save(unidadeEntity);

                    unidadeRepository.deleteLogico(id, tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade excluída com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    @Transactional
    public SuccessResponseDTO ativarUnidade(String id){
        String tenantId = TenantContext.getTenantId();
        return unidadeRepository.findById(id, tenantId)
                .map(unidade -> {
                    if(unidade.getStatus() != -1){
                        throw new UnidadeAtivaException("Unidade já está ativa!");

                    }
                    Unidades unidadeEntity = unidadeMapper.fromUnidadesViewToUnidades(unidade);
                    unidadeEntity.setDeletedAt(null);
                    unidadeRepository.save(unidadeEntity);

                    unidadeRepository.ativarUnidade(id, tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade ativada com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

}
