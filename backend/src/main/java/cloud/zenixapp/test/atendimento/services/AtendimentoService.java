package cloud.zenixapp.test.atendimento.services;

import cloud.zenixapp.test.atendimento.entities.Atendimento;
import cloud.zenixapp.test.atendimento.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;


    public Atendimento save(Atendimento atendimento){
        return atendimentoRepository.save(atendimento);
    }

}
