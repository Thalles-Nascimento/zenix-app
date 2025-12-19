package cloud.zenixapp.test.zenix.services;

import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;


    public Atendimento save(Atendimento atendimento){
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> findAll(){
        return atendimentoRepository.findAll();
    }

}
