package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.PagamentoMapper;
import cloud.zenixapp.zenix.configs.mappers.PlanosMapper;
import cloud.zenixapp.zenix.models.dtos.requests.PagamentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.PlanosRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.*;
import cloud.zenixapp.zenix.models.entities.FormaPagamento;
import cloud.zenixapp.zenix.models.entities.Planos;
import cloud.zenixapp.zenix.repositories.PagamentoRepository;
import cloud.zenixapp.zenix.repositories.PlanosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanosService {

    @Autowired
    private PlanosRepository planosRepository;

    @Autowired
    private PlanosMapper planosMapper;

    @Transactional
    public SuccessPlanosResponseDTO inserirPlano(PlanosRequestDTO planosRequestDTO){
        planosRepository.save(planosMapper.toPlanos(planosRequestDTO));

        return new SuccessPlanosResponseDTO(
                HttpStatus.OK.value(),
                "Plano inserido com sucesso"
        );
    }

    public List<PlanosResponseDTO> buscarTodosPlanos(){
        return planosMapper.toListPlanosDTO(planosRepository.findAll());
    }

    public Planos buscarPlanoPorId(String id){
        return planosRepository.findById(id).orElseThrow(() -> new NotFoundException("Plano não encontrado!"));
    }

    @Transactional
    public SuccessPlanosResponseDTO atualizarPlanos(PlanosRequestDTO planosRequestDTO, String id){
        return planosRepository.findById(id)
                .map(plano -> {

                    plano.setUpdatedAt(LocalDateTime.now());
                    planosMapper.atualizarPlano(plano, planosRequestDTO);
                    planosRepository.save(plano);

                    return new SuccessPlanosResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano atualizado com sucesso"
                    );
                }).orElseThrow(() -> new NotFoundException("Plano não encontrado!"));
    }

    @Transactional
    public SuccessDeletePlanosResponseDTO deletarPlano(String id) {
        return planosRepository.findById(id)
                .map(plano -> {
                    planosRepository.deleteById(id);

                    return new SuccessDeletePlanosResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Plano não encontrado!"));
    }

}
