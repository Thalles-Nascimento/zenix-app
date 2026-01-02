package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.dtos.AtendimentoUpdateRequestDTO;
import cloud.zenixapp.zenix.entities.Atendimento;
import cloud.zenixapp.zenix.exceptions.AtendimentoException;
import cloud.zenixapp.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AtendimentoResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        Atendimento atendimento = atendimentoMapper.paraEntity(atendimentoDTO);
        return atendimentoMapper.responseDTO(atendimentoRepository.save(atendimento));
    }

    public List<AtendimentoResponseDTO> listarAtendimentos(){
        return atendimentoMapper.listResponseDTO(atendimentoRepository.findAll());
    }

//  Optional sendo utilizado para evitar o NullPointerException (NPE)
    public Optional<AtendimentoResponseDTO> listarAtendimentoPorId(Long id){
        return atendimentoRepository.findById(id)
                .map((atendimento -> atendimentoMapper.responseDTO(atendimento)));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deletarAtendimento(Long id) throws AtendimentoException {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    if (atendimento.getStatus() == -1) {
                        return false;
                    }
                    atendimentoRepository.deleteLogico(id);
                    return true;
                })
                .orElseThrow(() -> new AtendimentoException("Não foi possível excluir atendimento!"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AtendimentoResponseDTO atualizarAtendimento(Long id, AtendimentoUpdateRequestDTO atendimentoDTO) throws AtendimentoException {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoDTO);
                    return atendimentoMapper.responseDTO(atendimentoRepository.save(atendimento));
                })
                .orElseThrow(() -> new AtendimentoException("Não foi possível atualizar"));
    }

}
