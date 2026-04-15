package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ExistsException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.ServicoExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.ServicoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.ServicoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ServicoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.Servicos;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.repositories.ServicoRepository;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ServicoMapper servicoMapper;

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public SuccessResponseDTO inserirServico(ServicoRequestDTO servicoRequestDTO){
        String tenantId = TenantContext.getTenantId();
        if (servicoRepository.existsServicoByServicoAndTenantId(servicoRequestDTO.servico(), tenantId)){
            throw new ExistsException("Serviço já existe!");

        }

        Servicos servico = servicoMapper.toServicos(servicoRequestDTO);
        Tenants tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant não encontrado!"));

        servico.setTenant(tenant);

        servicoRepository.save(servico);

        return new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Serviço inserido com sucesso"
        );
    }

    public List<ServicoResponseDTO> buscarTodosServicos(){
        return servicoMapper.toListServicos(servicoRepository.findAll(TenantContext.getTenantId()));
    }


    @Transactional
    public SuccessResponseDTO atualizarServico(ServicoRequestDTO servicoRequestDTO, String id){
        return servicoRepository.findById(id, TenantContext.getTenantId())
                .map(servico -> {
                    if (servico.getStatus() == -1){
                        throw new ServicoExcluidoException("Serviço foi excluído!");

                    }

                    servicoMapper.atualizarServico(servico, servicoRequestDTO);
                    servicoRepository.save(servico);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Serviço atualizado com sucesso"
                    );
                }).orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }


    @Transactional
    public SuccessResponseDTO deletarServico(String id) {
        return servicoRepository.findById(id, TenantContext.getTenantId())
                .map(servico -> {
                    if (servico.getStatus() == -1){
                        throw new ServicoExcluidoException("Serviço foi excluído!");
                    }

                    servico.setDeletedAt(LocalDateTime.now());
                    servicoRepository.deleteById(id);


                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Serviço excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado!"));
    }

}
