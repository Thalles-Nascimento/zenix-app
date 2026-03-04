package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.mappers.FilaMapper;
import cloud.zenixapp.zenix.models.dtos.requests.FilaRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.FilaResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SucessFilaResponseDTO;
import cloud.zenixapp.zenix.models.entities.Fila;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.FilaAtendimentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilaService {

    @Autowired
    private FilaAtendimentoRepository filaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FilaMapper filaMapper;

    @Transactional
    public SucessFilaResponseDTO inserirAtendimentoFila(FilaRequestDTO filaDTO){
        Fila fila = new Fila();
        Usuarios user = usuarioService.getUsuarioID(filaDTO.idBarbeiro());

        fila.setNomeCliente(filaDTO.nomeCliente());
        fila.setServico(filaDTO.servico());
        fila.setFormaPagamento(filaDTO.formaPagamento());
        fila.setUsuario(user);

        filaRepository.save(fila);

        return new SucessFilaResponseDTO(
                fila.getId(),
                fila.getNomeCliente(),
                fila.getServico(),
                fila.getStatus()
        );
    }

    public List<FilaResponseDTO> getFilasByUser(){
        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return filaMapper.toListFilaDTO(filaRepository.findByUser(userAuth.getId()));
    }

}
