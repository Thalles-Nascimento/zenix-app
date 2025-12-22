package cloud.zenixapp.test.zenix.services;

import cloud.zenixapp.test.zenix.entities.Atendimento;
import cloud.zenixapp.test.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Atendimento findById(Long id){
        Optional<Atendimento> atendimento = atendimentoRepository.findById(id);
        return atendimento.orElseThrow();
    }

    public String delete(Long id){
        atendimentoRepository.delete(this.findById(id));
        return "Atendimento excluido";
    }

}
