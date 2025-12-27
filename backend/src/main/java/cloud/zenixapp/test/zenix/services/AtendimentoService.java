package cloud.zenixapp.test.zenix.services;

import cloud.zenixapp.test.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.test.zenix.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.test.zenix.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.exceptions.AtendimentoException;
import cloud.zenixapp.test.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private AtendimentoMapper atendimentoMapper;

    public Atendimento inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        Atendimento atendimento = atendimentoMapper.insertAtendimento(atendimentoDTO);
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> listarAtendimentos(){
        return atendimentoRepository.findAll();
    }

    public Atendimento listarAtendimentoPorId(Long id) throws AtendimentoException {
        Optional<Atendimento> atendimento = atendimentoRepository.findById(id);
        atendimento.orElseThrow(() -> new AtendimentoException("Atendimento não existe"));
        return atendimento.get();
    }

    public boolean deletarAtendimento(Long id) {
        try {
            Atendimento atendimento = this.listarAtendimentoPorId(id);

            if(atendimento.getStatus() == -1){
                return false;
            }
            atendimentoRepository.deleteLogico(id);
            return true;

        } catch (AtendimentoException e) {
            return false;
        }

    }

    public Atendimento atualizarAtendimento(Long id, AtendimentoRequestDTO atendimentoDTO) throws AtendimentoException {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoDTO);
                    return atendimentoRepository.save(atendimento);
                })
                .orElseThrow(() -> new AtendimentoException("Não foi possível atualizar!"));
    }

}
