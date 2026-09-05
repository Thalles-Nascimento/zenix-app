package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.AtendimentoExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    private final DateTimeFormatter current_date = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private AtendimentoMapper atendimentoMapper;

    @Autowired
    private ClienteService clienteService;

    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        String tenantId = TenantContext.getTenantId();

        // Atualiza o atendimento usado como referência para o Plano e o retorno do cliente
        clienteService.atualizarRetornoDoCliente(atendimentoDTO.descricao(), tenantId);

        Usuarios userAuth = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Atendimento atendimento = atendimentoMapper.inserirAtendimento(atendimentoDTO);

        atendimento.setDate(LocalDateTime.now().format(current_date));
        atendimento.setUsuarios(userAuth);
        atendimento.setTenant(tenantId);


        atendimentoRepository.save(atendimento);

        return new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Atendimento inserido com sucesso!"
        );
    }

    public List<AtendimentoResponseDTO> listarAtendimentosHoje(){
        Usuarios user = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return atendimentoRepository.findByUsuariosAndDateAndTenant(user, LocalDateTime.now().format(current_date), TenantContext.getTenantId());
    }

//    TODO Criar uma tela no frontend para visualizar esses atendimentos abaixo
    public List<AtendimentoResponseDTO> listarTodosAtendimentos(){
        return atendimentoRepository.findAllByTenant(TenantContext.getTenantId());
    }


    public List<AtendimentoResponseDTO> listarHistorico(){
        Usuarios user = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return atendimentoRepository.findByUsuariosAndTenant(user, TenantContext.getTenantId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO deletarAtendimento(String id) {
        String tenantId = TenantContext.getTenantId();
        return atendimentoRepository.findByIdAndTenant(id, tenantId)
                .map(atendimento -> {
                    if (atendimento.status() == -1) {
                        throw new AtendimentoExcluidoException("Atendimento já foi excluído!");

                    }

                    clienteService.retiraRetornoCliente(atendimento.descricao(), tenantId);

                    atendimentoRepository.deleteLogico(atendimento.id(), LocalDateTime.now(), tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO atualizarAtendimento(String id, AtendimentoRequestDTO atendimentoRequestDTO){
        return atendimentoRepository.findById(id, TenantContext.getTenantId())
                .map(atendimento -> {
                    if(atendimento.getStatus() == -1){
                        throw new NotFoundException("Não é possível atualizar o atendimento!");
                    }

                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoRequestDTO);

                    atendimentoRepository.save(atendimento);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento atualizado com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    //    TODO Restringir a ativação apenas ao administrador
    @Transactional
    public SuccessResponseDTO ativarAtendimento(String id){
        String tenantId = TenantContext.getTenantId();
        return atendimentoRepository.findByIdAndTenant(id, tenantId)
                .map(atendimento -> {
                    if(atendimento.status() != -1){
                        throw new AtendimentoExcluidoException("Atendimento já está ativo!");
                    }

                    clienteService.atualizarRetornoDoCliente(atendimento.descricao(), tenantId);

                    atendimentoRepository.ativarAtendimento(atendimento.id(), tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento ativado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

}
