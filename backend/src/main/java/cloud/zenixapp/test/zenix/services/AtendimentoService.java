package cloud.zenixapp.test.zenix.services;

import cloud.zenixapp.test.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.test.zenix.dtos.AtendimentoDTO;
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

    public Atendimento save(Atendimento atendimento){
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> findAll(){
        return atendimentoRepository.findAll();
    }

    public Atendimento findById(Long id) throws AtendimentoException {
        Optional<Atendimento> atendimento = atendimentoRepository.findById(id);
        atendimento.orElseThrow(() -> {
            return new AtendimentoException("Atendimento não existe");
        });
        return atendimento.get();
    }

    public void delete(Long id) throws AtendimentoException {
        atendimentoRepository.delete(this.findById(id));
    }

    public Atendimento update(Long id, AtendimentoDTO atendimentoDTO) throws AtendimentoException {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    atendimentoMapper.updateFromDTO(atendimento, atendimentoDTO);
                    return atendimentoRepository.save(atendimento);
                })
                .orElseThrow(() -> new AtendimentoException("Não foi possível atualizar!"));
    }

}
