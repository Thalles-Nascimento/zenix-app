package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.AtivoException;
import cloud.zenixapp.zenix.configs.exceptions.ExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.AtendimentoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class AtendimentoService {

    private static final String MESSAGE_EXCEPTION_NOT_FOUND = "Atendimento não encontrado!";
    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final String  MESSAGE_EXCEPTION_EXCLUIDO = "Atendimento está excluído!";
    private final DateTimeFormatter currentDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AtendimentoRepository atendimentoRepository;

    private final AtendimentoMapper atendimentoMapper;

    private final ClienteService clienteService;

    public AtendimentoService(AtendimentoRepository atendimentoRepository, AtendimentoMapper atendimentoMapper, ClienteService clienteService) {
        this.atendimentoRepository = atendimentoRepository;
        this.atendimentoMapper = atendimentoMapper;
        this.clienteService = clienteService;
    }

    /*=====================================================================================
     * Regra para inserir um novo atendimento.
     *======================================================================================*/
    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        log.info("[SERVICE] Classe e Método: AtendimentoService.inserirAtendimento(Linha: 49)");
        String tenantId = TenantContext.getTenantId();

        // Atualiza o atendimento usado como referência para o Plano e o retorno do cliente
        clienteService.atualizarRetornoDoCliente(atendimentoDTO.descricao(), tenantId);

        Usuarios userAuth = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Atendimento atendimento = atendimentoMapper.inserirAtendimento(atendimentoDTO);

        atendimento.setDate(LocalDateTime.now(TIME_ZONE).format(currentDate));
        atendimento.setUsuarios(userAuth);
        atendimento.setTenant(tenantId);


        atendimentoRepository.save(atendimento);

        SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Atendimento inserido com sucesso!"
        );
        log.info("Atendimento inserido: [Status: {}] => [Message: {}]", successResponseDTO.status(), successResponseDTO.message());

        return successResponseDTO;
    }

    /*=====================================================================================
     * Regra para listar os atendimentos de hoje.
     *======================================================================================*/
    public List<AtendimentoResponseDTO> listarAtendimentosHoje(){
        log.info("[SERVICE] Classe e Método: AtendimentoService.listarAtendimentosHoje(Linha: 73)");
        Usuarios user = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        List<AtendimentoResponseDTO> atendimentoResponseDTOList = atendimentoRepository.findByUsuariosAndDateAndTenant(user, LocalDateTime.now(TIME_ZONE).format(currentDate), TenantContext.getTenantId());
        log.info("Atendimento encontrados de hoje: {}", atendimentoResponseDTOList.size());
        return atendimentoResponseDTOList;
    }

    /*=====================================================================================
     * Regra para listar todos os atendimentos do usuário.
     *======================================================================================*/
    public List<AtendimentoResponseDTO> listarTodosAtendimentos(){
        log.info("[SERVICE] Classe e Método: AtendimentoService.listarTodosAtendimentos(Linha: 78)");
        List<AtendimentoResponseDTO> atendimentoResponseDTOList = atendimentoRepository.findAllByTenant(TenantContext.getTenantId());
        log.info("Atendimento encontrados: {}", atendimentoResponseDTOList.size());
        return atendimentoResponseDTOList;
    }

    /*=====================================================================================
     * Regra para listar o histórico de atendimentos do usuário.
     *======================================================================================*/
    public List<AtendimentoResponseDTO> listarHistorico(){
        log.info("[SERVICE] Classe e Método: AtendimentoService.listarHistorico(Linha: 83)");
        Usuarios user = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        List<AtendimentoResponseDTO> atendimentoResponseDTOList = atendimentoRepository.findByUsuariosAndTenant(user, TenantContext.getTenantId());
        log.info("Atendimento encontrados no histórico: {}", atendimentoResponseDTOList.size());
        return atendimentoResponseDTOList;
    }

    /*=====================================================================================
     * Regra para deletar um atendimento pelo ID.
     *======================================================================================*/
    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO deletarAtendimento(String id) {
        log.info("[SERVICE] Classe e Método: AtendimentoService.deletarAtendimento(Linha: 89)");
        String tenantId = TenantContext.getTenantId();
        return atendimentoRepository.findByIdAndTenant(id, tenantId)
                .map(atendimento -> {
                    if (atendimento.status() == -1) {
                        log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.AtendimentoService.deletarAtendimento(String)");
                        throw new ExcluidoException(MESSAGE_EXCEPTION_EXCLUIDO);

                    }
                    log.info("Deletando o atendimento!");
                    clienteService.retiraRetornoCliente(atendimento.descricao(), tenantId);

                    atendimentoRepository.deleteLogico(atendimento.id(), LocalDateTime.now(TIME_ZONE), tenantId);

                    SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento deletado com sucesso!"
                    );

                    log.info("Atendimento deletado: [Status: {}] => [Message: {}]", successResponseDTO.status(), successResponseDTO.message());

                    return successResponseDTO;

                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    /*=====================================================================================
     * Regra para atualizar um atendimento pelo ID.
     *======================================================================================*/
    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO atualizarAtendimento(String id, AtendimentoRequestDTO atendimentoRequestDTO){
        log.info("[SERVICE] Classe e Método: AtendimentoService.atualizarAtendimento(Linha: 114)");
        return atendimentoRepository.findById(id, TenantContext.getTenantId())
                .map(atendimento -> {
                    if(atendimento.getStatus() == -1){
                        log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.AtendimentoService.atualizarAtendimento(String, AtendimentoRequestDTO)");
                        throw new ExcluidoException(MESSAGE_EXCEPTION_EXCLUIDO);

                    }
                    log.info("Atualizando o atendimento.");
                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoRequestDTO);

                    atendimentoRepository.save(atendimento);

                    SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento atualizado com sucesso!"
                    );

                    log.info("Atendimento atualizado: [Status: {}] => [Message: {}]", successResponseDTO.status(), successResponseDTO.message());

                    return successResponseDTO;

                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    /*=====================================================================================
     * Regra para ativar um atendimento pelo ID.
     *======================================================================================*/
    @Transactional
    public SuccessResponseDTO ativarAtendimento(String id){
        log.info("[SERVICE] Classe e Método: AtendimentoService.ativarAtendimento(Linha: 139)");
        String tenantId = TenantContext.getTenantId();
        return atendimentoRepository.findByIdAndTenant(id, tenantId)
                .map(atendimento -> {
                    if(atendimento.status() == 1){
                        log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.AtendimentoService.ativarAtendimento(String)");
                        throw new AtivoException("Atendimento já está ativo!");

                    }

                    log.info("Ativando o atendimento.");
                    clienteService.atualizarRetornoDoCliente(atendimento.descricao(), tenantId);

                    atendimentoRepository.ativarAtendimento(atendimento.id(), tenantId);

                    SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento ativado com sucesso!"
                    );

                    log.info("Atendimento ativado: [Status: {}] => [Message: {}]", successResponseDTO.status(), successResponseDTO.message());

                    return successResponseDTO;

                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

}
