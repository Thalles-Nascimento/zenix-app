package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.AtendimentoExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.interfaces.AtendimentoAndUsuarioProjectionView;
import cloud.zenixapp.zenix.models.interfaces.AtendimentoProjectionView;
import cloud.zenixapp.zenix.repositories.AtendimentoRepository;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    private final DateTimeFormatter current_date = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AtendimentoMapper atendimentoMapper;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        Tenants tenant = tenantRepository.findById(TenantContext.getTenantId())
                .orElseThrow(() -> new NotFoundException("Tenant não encontrado!"));

        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Atendimento atendimento = new Atendimento();

        atendimento.setDescricao(atendimentoDTO.descricao());
        atendimento.setServico(atendimentoDTO.servico());
        atendimento.setValor(atendimentoDTO.valor());
        atendimento.setFormaPagamento(atendimentoDTO.formaPagamento());
        atendimento.setObservacao(atendimentoDTO.observacao());
        atendimento.setDate(LocalDateTime.now().format(current_date));
        atendimento.setUsuarios(usuarioService.getUsuarioById(userAuth.getId()));
        atendimento.setTenant(tenant);

        if (!clienteService.buscarClientePorNome(atendimento.getDescricao()).isEmpty()){
            clienteService.atualizarAtendimentosMes(atendimentoDTO.descricao());
        }


        atendimentoRepository.save(atendimento);

        return new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Atendimento inserido com sucesso!"
        );
    }

    public List<AtendimentoProjectionView> listarAtendimentosHoje(){
        Usuarios user = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return atendimentoRepository.findByUserDate(user.getId(), LocalDateTime.now().format(current_date), TenantContext.getTenantId());
    }

//    TODO Criar uma tela no frontend para visualizar esses atendimentos abaixo
    public List<AtendimentoAndUsuarioProjectionView> listarTodosAtendimentos(){
        return atendimentoRepository.findAll(TenantContext.getTenantId());
    }

    public AtendimentoProjectionView listarAtendimentoPorId(String idAtendimento){
        Usuarios user = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return atendimentoRepository.findByUserById(user.getId(), idAtendimento, TenantContext.getTenantId())
                .map((atendimento -> {
                    if(atendimento.getStatus() == -1){
                        throw new NotFoundException("Atendimento foi excluído!");
                    }
                    return atendimento;

                }))
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    public List<AtendimentoProjectionView> listarHistorico(){
        Usuarios user = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return atendimentoRepository.findByUser(user.getId(), TenantContext.getTenantId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO deletarAtendimento(String id) {
        return atendimentoRepository.findById(id, TenantContext.getTenantId())
                .map(atendimento -> {
                    if (atendimento.getStatus() == -1) {
                        throw new AtendimentoExcluidoException("Atendimento já foi excluído!");

                    }
                    if (!clienteService.buscarClientePorNome(atendimento.getDescricao()).isEmpty()){
                        clienteService.retiraRetornoCliente(atendimento.getDescricao());
                    }

                    atendimentoRepository.deleteLogico(id, LocalDateTime.now(), TenantContext.getTenantId());

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

//    TODO Restringir a edição apenas ao administrador | Restringir a chamada no método atualizar por campos não alterados
    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO atualizarAtendimento(String id, AtendimentoRequestDTO atendimentoRequestDTO) throws NotFoundException {
        return atendimentoRepository.findByIdAtendimento(id)
                .map(atendimento -> {
                    if(atendimento.getStatus() == -1){
                        throw new NotFoundException("Não é possível atualizar um atendimento excluído!");
                    }

                    atendimento.setUpdatedAt(LocalDateTime.now());
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
        return atendimentoRepository.findById(id, TenantContext.getTenantId())
                .map(atendimento -> {
                    if(atendimento.getStatus() != -1){
                        throw new AtendimentoExcluidoException("Atendimento já está ativo!");
                    }

                    atendimentoRepository.ativarAtendimento(id, TenantContext.getTenantId());

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento ativado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

}
