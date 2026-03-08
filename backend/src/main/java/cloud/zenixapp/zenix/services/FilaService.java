package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.FilaException;
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

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalTime;
import java.util.List;

import static cloud.zenixapp.zenix.models.enums.StatusFilaEnum.AGUARDANDO;
import static cloud.zenixapp.zenix.models.enums.StatusFilaEnum.EM_ATENDIMENTO;

@Service
public class FilaService {

    @Autowired
    private FilaAtendimentoRepository filaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FilaMapper filaMapper;

    @Transactional
    public SucessFilaResponseDTO inserirAtendimentoFila(FilaRequestDTO filaDTO) throws SQLIntegrityConstraintViolationException {
        Fila fila = new Fila();
        Usuarios user = usuarioService.getUsuarioById(filaDTO.idBarbeiro());

        fila.setNomeCliente(filaDTO.nomeCliente());
        fila.setServico(filaDTO.servico());
        fila.setFormaPagamento(filaDTO.formaPagamento());
        fila.setTelefoneCliente(filaDTO.telefoneCliente());
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

    @Transactional
    public SucessFilaResponseDTO atualizarAtendimentoFila(Long id) {
        return filaRepository.findById(id)
                .map(atendimentoFila -> {
                    if(atendimentoFila.getStatus() != AGUARDANDO){
                        throw new FilaException("Clientes está Em Atendimento ou Finalizado");
                    }

                    filaRepository.paraAtendimento(id);
                    filaRepository.marcarHoraInicio(id, LocalTime.now());

                    return new SucessFilaResponseDTO(
                            atendimentoFila.getId(),
                            atendimentoFila.getNomeCliente(),
                            atendimentoFila.getServico(),
                            atendimentoFila.getStatus()
                    );
                })
                .orElseThrow();
    }

    @Transactional
    public SucessFilaResponseDTO finalizarAtendimento(Long id){
        return filaRepository.findById(id)
                .map(atendimentoFila -> {
                    if (atendimentoFila.getStatus() != EM_ATENDIMENTO){
                        throw new FilaException("Clientes já Finalizado ou está Aguardando");
                    }
                    filaRepository.finalizarAtendimentoFila(id);
                    filaRepository.marcarHoraFinal(id, LocalTime.now());

                    return new SucessFilaResponseDTO(
                            atendimentoFila.getId(),
                            atendimentoFila.getNomeCliente(),
                            atendimentoFila.getServico(),
                            atendimentoFila.getStatus()
                    );
                })
                .orElseThrow();
    }

}
