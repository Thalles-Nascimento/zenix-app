package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.FilaException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.models.dtos.requests.FilaRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.filas.SuccessFilaResponseDTO;
import cloud.zenixapp.zenix.models.entities.Fila;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.interfaces.FilaProjectionView;
import cloud.zenixapp.zenix.repositories.FilaAtendimentoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class FilaService {

    private static final String MESSAGE = "Atendimento não encontrado";
    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

    private final FilaAtendimentoRepository filaRepository;

    private final UsuarioService usuarioService;

    private final ClienteService clienteService;

    public FilaService(FilaAtendimentoRepository filaRepository, UsuarioService usuarioService, ClienteService clienteService) {
        this.filaRepository = filaRepository;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
    }

    @Transactional
    public SuccessFilaResponseDTO inserirAtendimentoFila(FilaRequestDTO filaDTO) {
        String tenant = TenantContext.getTenantId();

        Fila fila = new Fila(); // Verificar a existência do atendimento na fila!

        if(filaDTO.semPreferencia()){
            fila.setNomeCliente(filaDTO.nomeCliente());
            fila.setServico(filaDTO.servico());
            fila.setFormaPagamento(filaDTO.formaPagamento());
            fila.setTelefoneCliente(filaDTO.telefoneCliente());
            fila.setSemPreferencia(true);
            fila.setTenant(tenant);
            fila.setUsuario(null);

            filaRepository.save(fila);

            return new SuccessFilaResponseDTO(
                    fila.getId(),
                    fila.getNomeCliente(),
                    fila.getServico(),
                    fila.getStatus()
            );
        }

        Usuarios user = usuarioService.getUsuarioById(filaDTO.idBarbeiro());

        fila.setNomeCliente(filaDTO.nomeCliente());
        fila.setServico(filaDTO.servico());
        fila.setFormaPagamento(filaDTO.formaPagamento());
        fila.setTelefoneCliente(filaDTO.telefoneCliente());
        fila.setUsuario(user);
        fila.setTenant(tenant);

        filaRepository.save(fila);


        return new SuccessFilaResponseDTO(
                fila.getId(),
                fila.getNomeCliente(),
                fila.getServico(),
                fila.getStatus()
        );
    }

    public List<FilaProjectionView> getFilasByUser(){
        Usuarios userAuth = (Usuarios) Objects
                .requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                .getPrincipal();
        if (userAuth != null){
            log.info("Usuário encontrado no contexto: {}", userAuth.getNome());
            return filaRepository.findByUser(userAuth.getId(), TenantContext.getTenantId());

        }
        log.error("Usuário não encontrado no SecurityContext");
        return Collections.emptyList();
    }

    @Transactional
    public SuccessResponseDTO chamarCliente(String id) {
        String tenantId = TenantContext.getTenantId();
        return filaRepository.findById(id, tenantId)
                .map(atendimentoFila -> {
                    if(atendimentoFila.getStatus() != 0){
                        throw new FilaException("Cliente está em atendimento ou já foi finalizado");
                    }

                    filaRepository.paraAtendimento(atendimentoFila.getId(), tenantId, LocalTime.now(TIME_ZONE));

                    if (atendimentoFila.getSemPreferencia()) {
                        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        filaRepository.setarUsuario(atendimentoFila.getId(), tenantId, userAuth.getId());
                    }

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente chamado!"
                    );
                })
                .orElseThrow(() -> new NotFoundException(MESSAGE));
    }

    @Transactional
    public SuccessResponseDTO finalizarAtendimento(String id){
        String tenantId = TenantContext.getTenantId();
        return filaRepository.findById(id, tenantId)
                .map(atendimentoFila -> {
                    if (atendimentoFila.getStatus() != 1){
                        throw new FilaException("Clientes já Finalizado ou está Aguardando");
                    }
                    filaRepository.finalizarAtendimentoFila(atendimentoFila.getId(), tenantId, LocalTime.now(TIME_ZONE));

                    clienteService.atualizarRetornoDoCliente(atendimentoFila.getNomeCliente(), tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento finalizado!"
                    );
                })
                .orElseThrow(() -> new NotFoundException(MESSAGE));
    }

    @Transactional
    public SuccessResponseDTO retirarClienteFila(String id) {
        String tenantId = TenantContext.getTenantId();
        return filaRepository.findById(id, tenantId)
                .map(atendimentoFila -> {
                    if (atendimentoFila.getStatus() == 1) {
                        throw new FilaException("Cliente está em atendimento");
                    }

                    filaRepository.deleteById(atendimentoFila.getId());

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente retirado da Fila"
                    );


                })
                .orElseThrow(() -> new NotFoundException(MESSAGE));
    }
}
