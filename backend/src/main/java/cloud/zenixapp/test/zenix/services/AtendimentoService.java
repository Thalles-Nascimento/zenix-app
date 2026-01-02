package cloud.zenixapp.test.zenix.services;

import cloud.zenixapp.test.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.test.zenix.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.test.zenix.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.test.zenix.dtos.AtendimentoUpdateRequestDTO;
import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.exceptions.AtendimentoException;
import cloud.zenixapp.test.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
