package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.FilaException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.FilaMapper;
import cloud.zenixapp.zenix.models.dtos.requests.FilaRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.FilaResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessFilaResponseDTO;
import cloud.zenixapp.zenix.models.entities.Fila;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.FilaAtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static cloud.zenixapp.zenix.models.enums.StatusFilaEnum.AGUARDANDO;
import static cloud.zenixapp.zenix.models.enums.StatusFilaEnum.EM_ATENDIMENTO;

@Service
public class FilaService {

    @Autowired
    private FilaAtendimentoRepository filaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FilaMapper filaMapper;

    @Transactional
    public SuccessFilaResponseDTO inserirAtendimentoFila(FilaRequestDTO filaDTO) throws SQLIntegrityConstraintViolationException {
        Fila fila = new Fila();
        Usuarios user = usuarioService.getUsuarioById(filaDTO.idBarbeiro());

        fila.setNomeCliente(filaDTO.nomeCliente());
        fila.setServico(filaDTO.servico());
        fila.setFormaPagamento(filaDTO.formaPagamento());
        fila.setTelefoneCliente(filaDTO.telefoneCliente());
        fila.setUsuario(user);


        filaRepository.save(fila);


        return new SuccessFilaResponseDTO(
                fila.getId(),
                fila.getNomeCliente(),
                fila.getServico(),
                fila.getStatus()
        );
    }

    @Transactional
    public List<SuccessFilaResponseDTO> inserirSemPreferencia(FilaRequestDTO filaDTO) {
        List<Usuarios> barbeiros = usuarioService.buscarUsuariosByUnidadesForLoginFila(filaDTO.idUnidade());
        String grupoId = UUID.randomUUID().toString();

        return barbeiros.stream().map(barbeiro -> {
            Fila fila = new Fila();
            fila.setNomeCliente(filaDTO.nomeCliente());
            fila.setServico(filaDTO.servico());
            fila.setFormaPagamento(filaDTO.formaPagamento());
            fila.setTelefoneCliente(filaDTO.telefoneCliente());
            fila.setUsuario(barbeiro);
            fila.setSemPreferencia(true);
            fila.setGrupoId(grupoId);
            filaRepository.save(fila);
            return new SuccessFilaResponseDTO(fila.getId(), fila.getNomeCliente(), fila.getServico(), fila.getStatus());
        }).toList();
    }

    public List<FilaResponseDTO> getFilasByUser(){
        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return filaMapper.toListFilaDTO(filaRepository.findByUser(userAuth.getId()));
    }

    @Transactional
    public SuccessFilaResponseDTO chamarCliente(String id) {
        return filaRepository.findById(id)
                .map(atendimentoFila -> {
                    if(atendimentoFila.getStatus() != AGUARDANDO){
                        throw new FilaException("Clientes está Em Atendimento ou Finalizado");
                    }
                    Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                    filaRepository.paraAtendimento(id);
                    filaRepository.marcarHoraInicio(id, LocalTime.now());

                    if (atendimentoFila.isSemPreferencia() && atendimentoFila.getGrupoId() != null) {
                        filaRepository.deletarOutrosDoGrupo(atendimentoFila.getGrupoId(), id);
                        filaRepository.setarUsuario(id, userAuth.getId());
                    }

                    return new SuccessFilaResponseDTO(
                            atendimentoFila.getId(),
                            atendimentoFila.getNomeCliente(),
                            atendimentoFila.getServico(),
                            atendimentoFila.getStatus()
                    );
                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado"));
    }

    @Transactional
    public SuccessFilaResponseDTO finalizarAtendimento(String id){
        return filaRepository.findById(id)
                .map(atendimentoFila -> {
                    if (atendimentoFila.getStatus() != EM_ATENDIMENTO){
                        throw new FilaException("Clientes já Finalizado ou está Aguardando");
                    }
                    filaRepository.finalizarAtendimentoFila(id);
                    filaRepository.marcarHoraFinal(id, LocalTime.now());

                    clienteService.atualizarAtendimentosMes(atendimentoFila.getNomeCliente());

                    return new SuccessFilaResponseDTO(
                            atendimentoFila.getId(),
                            atendimentoFila.getNomeCliente(),
                            atendimentoFila.getServico(),
                            atendimentoFila.getStatus()
                    );
                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado"));
    }

    @Transactional
    public SuccessResponseDTO retirarClienteFila(String id) {
        return filaRepository.findById(id)
                .map(atendimentoFila -> {
                    if (atendimentoFila.getStatus() == EM_ATENDIMENTO) {
                        throw new FilaException("Cliente está em atendimento");
                    }

                    String statusMsg = clienteService.retiraRetornoCliente(atendimentoFila.getNomeCliente());

                    if (atendimentoFila.isSemPreferencia() && atendimentoFila.getGrupoId() != null) {
                        filaRepository.deletarOutrosDoGrupo(atendimentoFila.getGrupoId(), id);
                    }

                    filaRepository.deleteById(id);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            statusMsg
                    );


                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado"));
    }
}
