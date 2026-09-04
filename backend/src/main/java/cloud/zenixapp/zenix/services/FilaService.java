package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.FilaException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.FilaMapper;
import cloud.zenixapp.zenix.models.dtos.requests.FilaRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.FilaResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessFilaResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.Fila;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.interfaces.FilaProjectionView;
import cloud.zenixapp.zenix.repositories.FilaAtendimentoRepository;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ClienteService clienteService;

    @Autowired
    private FilaMapper filaMapper;

    @Autowired
    private TenantRepository tenantRepository;

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
        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return filaRepository.findByUser(userAuth.getId(), TenantContext.getTenantId());
    }

    @Transactional
    public SuccessResponseDTO chamarCliente(String id) {
        String tenantId = TenantContext.getTenantId();
        return filaRepository.findById(id, tenantId)
                .map(atendimentoFila -> {
                    if(atendimentoFila.getStatus() != 0){
                        throw new FilaException("Cliente está em atendimento ou já foi finalizado");
                    }

                    filaRepository.paraAtendimento(atendimentoFila.getId(), tenantId, LocalTime.now());

                    if (atendimentoFila.getSemPreferencia()) {
                        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        filaRepository.setarUsuario(atendimentoFila.getId(), tenantId, userAuth.getId());
                    }

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente chamado!"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado"));
    }

    @Transactional
    public SuccessResponseDTO finalizarAtendimento(String id){
        String tenantId = TenantContext.getTenantId();
        return filaRepository.findById(id, tenantId)
                .map(atendimentoFila -> {
                    if (atendimentoFila.getStatus() != 1){
                        throw new FilaException("Clientes já Finalizado ou está Aguardando");
                    }
                    filaRepository.finalizarAtendimentoFila(atendimentoFila.getId(), tenantId, LocalTime.now());

                    clienteService.atualizarAtendimentosMes(atendimentoFila.getNomeCliente(), tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento finalizado!"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado"));
    }

    @Transactional
    public SuccessResponseDTO retirarClienteFila(String id) {
        String tenantId = TenantContext.getTenantId();
        return filaRepository.findById(id, tenantId)
                .map(atendimentoFila -> {
                    if (atendimentoFila.getStatus() == 1) {
                        throw new FilaException("Cliente está em atendimento");
                    }

                    String statusMsg = clienteService.retiraRetornoCliente(atendimentoFila.getNomeCliente(), tenantId);

                    filaRepository.deleteById(atendimentoFila.getId());

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            statusMsg
                    );


                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado"));
    }
}
