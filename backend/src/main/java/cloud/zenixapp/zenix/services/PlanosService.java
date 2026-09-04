package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.PlanosMapper;
import cloud.zenixapp.zenix.models.dtos.requests.PlanosRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.PlanosResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.Planos;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.interfaces.PlanosView;
import cloud.zenixapp.zenix.repositories.PlanosRepository;
import cloud.zenixapp.zenix.repositories.TenantRepository;
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

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public SuccessResponseDTO inserirPlano(PlanosRequestDTO planosRequestDTO){
        Planos plano = planosMapper.toPlanos(planosRequestDTO);

        String tenant = TenantContext.getTenantId();

        plano.setTenant(tenant);
        planosRepository.save(plano);

        return new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Plano inserido com sucesso"
        );
    }

    public List<PlanosView> buscarTodosPlanos(){
        return planosRepository.findAll(TenantContext.getTenantId());
    }

    public Planos buscarPlanoPorId(String id){
        return planosRepository.findPlanosById(id, TenantContext.getTenantId())
                .orElseThrow(() -> new NotFoundException("Plano não encontrado!"));
    }

    @Transactional
    public SuccessResponseDTO atualizarPlanos(PlanosRequestDTO planosRequestDTO, String id){
        return planosRepository.findById(id, TenantContext.getTenantId())
                .map(planoView -> {

                    Planos plano = planosMapper.fromPlanosViewtoPlanos(planoView);

                    planosMapper.atualizarPlano(plano, planosRequestDTO);
                    planosRepository.save(plano);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano atualizado com sucesso"
                    );
                }).orElseThrow(() -> new NotFoundException("Plano não encontrado!"));
    }

    @Transactional
    public SuccessResponseDTO deletarPlano(String id) {
        return planosRepository.findById(id, TenantContext.getTenantId())
                .map(plano -> {

                    planosRepository.deleteById(id); // TODO refazer o delete

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Plano não encontrado!"));
    }

}
