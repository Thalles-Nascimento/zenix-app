package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UnidadeAtivaException;
import cloud.zenixapp.zenix.configs.exceptions.UnidadeExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.UnidadeJaExisteException;
import cloud.zenixapp.zenix.configs.mappers.UnidadeMapper;
import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.*;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    public SuccessUnidadeResponseDTO inserirUnidade(UnidadeRequestDTO unidadeDTO){
        String tenantId = TenantContext.getTenantId();

        if (unidadeRepository.buscarUnidadesPorTenant(unidadeDTO.nomeUnidade(), tenantId).isPresent()){
            throw new UnidadeJaExisteException("Unidade " + unidadeDTO.nomeUnidade() + " já cadastrada");
        }

        Tenants tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        Unidades unidade = unidadeMapper.toUnidade(unidadeDTO);
        unidade.setTenant(tenant);

        unidadeRepository.save(unidade);

        return new SuccessUnidadeResponseDTO(
                HttpStatus.OK.value(),
                "Unidade inserida com sucesso",
                unidade
        );
    }

    public List<UnidadeResponseDTO> listarUnidades(){
        return unidadeMapper.toListUnidadeDTO(unidadeRepository.buscarUnidadesPorTenant(TenantContext.getTenantId()));
    }

    public UnidadeResponseDTO listarUnidadeById(Long id){
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade foi excluída!");

                    }

                    return unidadeMapper.toDTO(unidade);

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    public UnidadeUserResponseDTO listarUnidadesByIdUsuario(Long id){
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

    public Unidades listarUnidadeByIdCompleto(Long id){
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade foi excluída!");

                    }

                    return unidade;

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    @Transactional
    public SuccessUnidadeResponseDTO atualizarUnidade(Long id, UnidadeRequestDTO unidadeDTO){
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new NotFoundException("Unidade foi excluída!");

                    }

                    unidadeMapper.atualizarUnidade(unidade, unidadeDTO);
                    unidadeRepository.save(unidade);


                    return new SuccessUnidadeResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade atualizada com sucesso",
                            unidade
                    );

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    @Transactional
    public SuccessUnidadeDeleteResponseDTO deletarUnidade(Long id){
        String tenantId = TenantContext.getTenantId();
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade já foi excluída!");

                    }
                    unidade.setDeletedAt(LocalDateTime.now());
                    unidadeRepository.deleteLogico(id, tenantId);
                    return new SuccessUnidadeDeleteResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade excluída com sucesso",
                            unidade.getNomeUnidade()
                    );
                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    @Transactional
    public SuccessUnidadeResponseDTO ativarUnidade(Long id){
        String tenantId = TenantContext.getTenantId();
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() != -1){
                        throw new UnidadeAtivaException("Unidade já está ativa!");

                    }
                    unidadeRepository.ativarUnidade(id, tenantId);

                    return new SuccessUnidadeResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade ativada com sucesso",
                            unidade
                    );
                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

}
