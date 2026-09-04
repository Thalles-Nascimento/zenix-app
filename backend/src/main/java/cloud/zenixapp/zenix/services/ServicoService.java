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
import cloud.zenixapp.zenix.models.interfaces.ServicosSimplesView;
import cloud.zenixapp.zenix.models.interfaces.ServicosView;
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
        if (servicoRepository.existsServicoByServicoAndTenant(servicoRequestDTO.servico(), tenantId)){
            throw new ExistsException("Serviço já existe!");

        }

        Servicos servico = servicoMapper.toServicos(servicoRequestDTO);
        servico.setTenant(tenantId);

        servicoRepository.save(servico);

        return new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Serviço inserido com sucesso"
        );
    }

    public List<ServicosSimplesView> buscarTodosServicos(){
        return servicoRepository.findAll(TenantContext.getTenantId());
    }

//  TODO Refazer a projeção - retirar o deleted_at
    public ServicosView listarServicoPorId(String id){
        return servicoRepository.findById(id, TenantContext.getTenantId())
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }


    @Transactional
    public SuccessResponseDTO atualizarServico(ServicoRequestDTO servicoRequestDTO, String id){
        return servicoRepository.findById(id, TenantContext.getTenantId())
                .map(servicoView -> {
                    Servicos servico = servicoMapper.fromServicosViewToServicos(servicoView);

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
        String tenantId = TenantContext.getTenantId();
        return servicoRepository.findById(id, tenantId)
                .map(servicoView -> {

                    servicoRepository.deleteByIdAndTenant(id, tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Serviço excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado!"));
    }

}
