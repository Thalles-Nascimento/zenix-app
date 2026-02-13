package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.AtendimentoExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.SucessAtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private AtendimentoMapper atendimentoMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public SucessAtendimentoResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        atendimentoRepository.save(atendimentoMapper.paraEntity(atendimentoDTO));

        return new SucessAtendimentoResponseDTO(
                HttpStatus.CREATED.value(),
                "Atendimento inserido com sucesso!"
        );
    }

    public List<AtendimentoResponseDTO> listarAtendimentos(){
        return atendimentoMapper.listResponseDTO(atendimentoRepository.findAll());
    }

    public AtendimentoResponseDTO listarAtendimentoPorId(Long id){
        return atendimentoRepository.findById(id)
                .map((atendimento -> {
                    AtendimentoResponseDTO atendimentoResponseDTO = atendimentoMapper.responseDTO(atendimento);
                    if(atendimentoResponseDTO.status() == -1){
                        throw new NotFoundException("Atendimento foi excluído!");
                    }
                    return atendimentoResponseDTO;

                }))
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SucessAtendimentoResponseDTO deletarAtendimento(Long id) {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    if (atendimento.getStatus() == -1) {
                        throw new AtendimentoExcluidoException("Atendimento já foi excluído!");

                    }

                    atendimentoRepository.deleteLogico(id);
                    return new SucessAtendimentoResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SucessAtendimentoResponseDTO atualizarAtendimento(Long id, AtendimentoRequestDTO atendimentoRequestDTO) throws NotFoundException {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    if(atendimento.getStatus() == -1){
                        throw new NotFoundException("Não é possível atualizar um atendimento excluído!");
                    }

                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoRequestDTO);
                    return new SucessAtendimentoResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento atualizado com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

}
