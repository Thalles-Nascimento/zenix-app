package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.ServicoExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.ServicoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.ServicoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ServicoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessDeleteServicoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessServicoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Servicos;
import cloud.zenixapp.zenix.repositories.ServicoRepository;
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

    @Transactional
    public SuccessServicoResponseDTO inserirServico(ServicoRequestDTO servicoRequestDTO){
        Servicos servico = servicoRepository.save(servicoMapper.toServicos(servicoRequestDTO));

        return new SuccessServicoResponseDTO(
                HttpStatus.OK.value(),
                "Serviço inserido com sucesso",
                servico
        );
    }

    public List<ServicoResponseDTO> buscarTodosServicos(){
        return servicoMapper.toListServicos(servicoRepository.findAll());
    }

    @Transactional
    public SuccessServicoResponseDTO atualizarServico(ServicoRequestDTO servicoRequestDTO, String id){
        return servicoRepository.findById(id)
                .map(servico -> {
                    if (servico.getStatus() == -1){
                        throw new ServicoExcluidoException("Serviço foi excluído!");
                    }

                    servico.setUpdatedAt(LocalDateTime.now());
                    servicoMapper.atualizarServico(servico, servicoRequestDTO);
                    servicoRepository.save(servico);

                    return new SuccessServicoResponseDTO(
                            HttpStatus.OK.value(),
                            "Serviço atualizado com sucesso",
                            servico
                    );
                }).orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }

    @Transactional
    public SuccessDeleteServicoResponseDTO deletarServico(String id) {
        return servicoRepository.findById(id)
                .map(servico -> {
                    servicoRepository.deleteById(id);

                    return new SuccessDeleteServicoResponseDTO(
                            HttpStatus.OK.value(),
                            "Serviço excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado!"));
    }

}
