package cloud.zenixapp.test.zenix.services;

import cloud.zenixapp.test.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.test.zenix.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.test.zenix.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.exceptions.AtendimentoException;
import cloud.zenixapp.test.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private AtendimentoMapper atendimentoMapper;

    @Transactional
    public AtendimentoResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        Atendimento atendimento = atendimentoMapper.insertAtendimento(atendimentoDTO);
        return atendimentoMapper.responseDTO(atendimentoRepository.save(atendimento));
    }

    public List<Atendimento> listarAtendimentos(){
        return atendimentoRepository.findAll();
    }

    public Optional<AtendimentoResponseDTO> listarAtendimentoPorId(Long id){
        return atendimentoRepository.findById(id)
                .map((atendimento -> atendimentoMapper.responseDTO(atendimento)));
    }

    @Transactional
    public boolean deletarAtendimento(Long id) {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    if (atendimento.getStatus() == -1) {
                        return false;
                    }
                    atendimentoRepository.deleteLogico(id);
                    return true;
                })
                .orElse(false);


    }

    @Transactional
    public Atendimento atualizarAtendimento(Long id, AtendimentoRequestDTO atendimentoDTO) throws AtendimentoException {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoDTO);
                    return atendimentoRepository.save(atendimento);
                })
                .orElseThrow(() -> new AtendimentoException("Não foi possível atualizar!"));
    }

}
