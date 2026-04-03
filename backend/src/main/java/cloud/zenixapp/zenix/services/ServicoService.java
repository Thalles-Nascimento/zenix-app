package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ExistsException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.ServicoExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.OptimisticException;
import cloud.zenixapp.zenix.configs.mappers.ServicoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.ServicoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ServicoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessDeleteServicoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessServicoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Servicos;
import cloud.zenixapp.zenix.repositories.ServicoRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ServicoMapper servicoMapper;

    @Transactional
    public SuccessServicoResponseDTO inserirServico(ServicoRequestDTO servicoRequestDTO){
        String tenantId = TenantContext.getTenantId();
        if (servicoRepository.existsServicoByServicoAndTenantId(servicoRequestDTO.servico(), tenantId)){
            throw new ExistsException("Serviço já existe!");

        }

        Servicos servico = servicoMapper.toServicos(servicoRequestDTO);
        servico.setTenantId(tenantId);

        servicoRepository.save(servico);

        return new SuccessServicoResponseDTO(
                HttpStatus.OK.value(),
                "Serviço inserido com sucesso",
                servicoMapper.toServicoDTO(servico)
        );
    }

    public List<ServicoResponseDTO> buscarTodosServicos(){
        return servicoMapper.toListServicos(servicoRepository.findAll(TenantContext.getTenantId()));
    }

    @Transactional
    public SuccessServicoResponseDTO atualizarServico(ServicoRequestDTO servicoRequestDTO, String id){
        return servicoRepository.findById(id, TenantContext.getTenantId())
                .map(servico -> {
                    if (servico.getStatus() == -1){
                        throw new ServicoExcluidoException("Serviço foi excluído!");
                    }

                    try {
                        servicoMapper.atualizarServico(servico, servicoRequestDTO);
                        servicoRepository.save(servico);

                    } catch (OptimisticLockException e){
                        throw new OptimisticException("Erro ao atualizar: " + e + ", tente novamente!");

                    }


                    return new SuccessServicoResponseDTO(
                            HttpStatus.OK.value(),
                            "Serviço atualizado com sucesso",
                            servicoMapper.toServicoDTO(servico)
                    );
                }).orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }

    @Transactional
    public SuccessDeleteServicoResponseDTO deletarServico(String id) {
        return servicoRepository.findById(id, TenantContext.getTenantId())
                .map(servico -> {
                    if (servico.getStatus() == -1){
                        throw new ServicoExcluidoException("Serviço foi excluído!");
                    }

                    try {
                        servicoRepository.deleteById(id);

                    } catch (OptimisticLockException e){
                        throw new OptimisticException("Erro ao deletar: " + e + ", tente novamente!");

                    }

                    return new SuccessDeleteServicoResponseDTO(
                            HttpStatus.OK.value(),
                            "Serviço excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado!"));
    }

}
