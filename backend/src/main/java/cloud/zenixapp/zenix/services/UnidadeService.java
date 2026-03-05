package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UnidadeExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.UsuarioExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.UnidadeMapper;
import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.*;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnidadeService {

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private UnidadeMapper unidadeMapper;

    @Transactional
    public SuccessUnidadeResponseDTO inserirUnidade(UnidadeRequestDTO unidadeDTO){
        Unidades unidade = unidadeRepository.save(unidadeMapper.toUnidade(unidadeDTO));

        return new SuccessUnidadeResponseDTO(
                HttpStatus.OK.value(),
                "Unidade inserida com sucesso",
                unidade
        );
    }

    public List<UnidadeResponseDTO> listarUnidades(){
        return unidadeMapper.toListUnidadeDTO(unidadeRepository.findAll());
    }

    public UnidadeResponseDTO listarUnidadeById(Long id){
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade foi excluída!");

                    }

                    return unidadeMapper.toDTO(unidade);

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    public Unidades listarUnidadeByIdCompleto(Long id){
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade foi excluída!");

                    }

                    return unidade;

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    @Transactional
    public SuccessUnidadeResponseDTO atualizarUnidade(Long id, UnidadeRequestDTO unidadeDTO){
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new NotFoundException("Unidade foi excluída!");

                    }

                    unidadeMapper.atualizarUnidade(unidade, unidadeDTO);


                    return new SuccessUnidadeResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade atualizada com sucesso",
                            unidade
                    );

                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

    @Transactional
    public SuccessUnidadeDeleteResponseDTO deletarUnidade(Long id){
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    if(unidade.getStatus() == -1){
                        throw new UnidadeExcluidoException("Unidade já foi excluída!");

                    }

                    unidadeRepository.deleteLogico(id);
                    return new SuccessUnidadeDeleteResponseDTO(
                            HttpStatus.OK.value(),
                            "Unidade deletada com sucesso",
                            unidade.getNomeUnidade()
                    );
                })
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada!"));
    }

}
