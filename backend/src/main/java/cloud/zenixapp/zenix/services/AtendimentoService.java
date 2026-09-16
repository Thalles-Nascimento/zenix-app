package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ConflictException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Tenants;
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


/**
 * <h2>
 *     Serviço que estabelece as regras de negócio do Domínio Atendimento.
 * </h2>
 * Métodos suportados:
 * {@link AtendimentoService#inserirAtendimento(AtendimentoRequestDTO) Inserir Atendimento}
 * {@link AtendimentoService#listarAtendimentosHojeByUsuario() Listar Atendimentos do Dia de um Usuário}
 * {@link AtendimentoService#listarTodosAtendimentos() Listar Atendimentos}
 * {@link AtendimentoService#listarHistorico() Listar Histórico de Atendimentos de um Usuário}
 * {@link AtendimentoService#deletarAtendimento(String) Deletar Atendimento}
 * {@link AtendimentoService#atualizarAtendimento(String, AtendimentoRequestDTO) Atualizar Atendimento}
 * {@link AtendimentoService#ativarAtendimento(String) Ativar Atendimento}
 *
 * @version 1.0
 * @author Thalles Nascimento
 */
@Log4j2
@Service
public class AtendimentoService {

    // Mensagem padrão para exceções onde o objeto não foi encontrado.
    private static final String MESSAGE_EXCEPTION_NOT_FOUND = "Atendimento não encontrado!";

    // TimeZone padrão para as funções de LocalDateTime.now().
    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

    // Mensagem padrão para exceções onde o objeto foi excluído.
    private static final String  MESSAGE_EXCEPTION_EXCLUIDO = "Atendimento está excluído!";

    // Formatador de data para o padrão brasileiro.
    private final DateTimeFormatter currentDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AtendimentoRepository atendimentoRepository;
    private final AtendimentoMapper atendimentoMapper;
    private final ClienteService clienteService;

    /**
     * <h2>
     *     Construtor padrão da classe AtendimentoService para automatizar a injeção de dependência gerenciada pelo Spring.
     * </h2>
     *
     * @param atendimentoRepository Repositório do domínio atendimento utilizado para realizar consultas no banco de dados.
     * @param atendimentoMapper Mapeia uma entidade para um DTO e vice-versa.
     * @param clienteService Classe responsável pelas regras de negócio do domínio cliente.
     * @see AtendimentoRepository
     * @see AtendimentoMapper
     * @see ClienteService
     * @see Tenants
     */
    public AtendimentoService(AtendimentoRepository atendimentoRepository, AtendimentoMapper atendimentoMapper, ClienteService clienteService) {
        this.atendimentoRepository = atendimentoRepository;
        this.atendimentoMapper = atendimentoMapper;
        this.clienteService = clienteService;
    }

    /**
     * <h2>
     *     Método para inserir um atendimento.
     * </h2>
     * <p>
     *     Este método é utilizado para inserção de um novo atendimento no sistema. Ele utiliza o método
     *     {@link ClienteService#atualizarRetornoDoCliente(String, String) Atualizar Retorno Cliente} para atualizar o retorno do cliente naquela barbearia.
     * </p>
     * @param atendimentoDTO DTO responsável pela exposição dos dados necessários para inserção do atendimento.
     * @return {@link SuccessResponseDTO}
     * @see Transactional
     * @see SuccessResponseDTO
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        log.info("[SERVICE -> Inserir atendimento] : AtendimentoService.inserirAtendimento(Linha: 49)");
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

    /**
     * <h2>
     *     Método para listar os atendimentos do dia de um usuário.
     * </h2>
     * <p>
     *     Os atendimentos do dia são mostrados para o usuário que realizou a requisição, ou seja, o que está no contexto de segurança do sistema.
     * </p>
     * @return List<{@link AtendimentoResponseDTO}> - Pode retornar uma lista vazia.
     * @see AtendimentoResponseDTO
     */
    public List<AtendimentoResponseDTO> listarAtendimentosHojeByUsuario(){
        log.info("[SERVICE -> Listar atendimento de hoje] : AtendimentoService.listarAtendimentosHoje(Linha: 73)");
        Usuarios user = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        List<AtendimentoResponseDTO> atendimentoResponseDTOList = atendimentoRepository.findByUsuariosAndDateAndTenant(user, LocalDateTime.now(TIME_ZONE).format(currentDate), TenantContext.getTenantId());
        log.info("Atendimento encontrados de hoje: {}", atendimentoResponseDTOList.size());
        return atendimentoResponseDTOList;
    }

    /**
     * <h2>
     *     Método para listar todos os atendimentos.
     * </h2>
     * <p>
     *     Este método é requisitado pelo administrador para listar todos os atendimentos da sua barbearia.
     * </p>
     * @return List<{@link AtendimentoResponseDTO}> - Pode retornar uma lista vazia.
     * @see AtendimentoResponseDTO
     */
    public List<AtendimentoResponseDTO> listarTodosAtendimentos(){
        log.info("[SERVICE -> Listar todos os atendimentos] : AtendimentoService.listarTodosAtendimentos(Linha: 78)");
        List<AtendimentoResponseDTO> atendimentoResponseDTOList = atendimentoRepository.findAllByTenant(TenantContext.getTenantId());
        log.info("Atendimento encontrados: {}", atendimentoResponseDTOList.size());
        return atendimentoResponseDTOList;
    }

    /**
     * <h2>
     *     Método para listar todos os atendimentos de um usuário.
     * </h2>
     * <p>
     *     Este método lista o histórico de atendimentos de um usuário.
     * </p>
     * @return List<{@link AtendimentoResponseDTO}> - Pode retornar uma lista vazia.
     * @see AtendimentoResponseDTO
     */
    public List<AtendimentoResponseDTO> listarHistorico(){
        log.info("[SERVICE -> Listar histórico] : AtendimentoService.listarHistorico(Linha: 83)");
        Usuarios user = (Usuarios) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        List<AtendimentoResponseDTO> atendimentoResponseDTOList = atendimentoRepository.findByUsuariosAndTenant(user, TenantContext.getTenantId());
        log.info("Atendimento encontrados no histórico: {}", atendimentoResponseDTOList.size());
        return atendimentoResponseDTOList;
    }

    /**
     * <h2>
     *     Método para deletar um atendimento.
     * </h2>
     * <h6>Apenas o administrador pode deletar um atendimento</h6>
     * <p>
     *     Este método é utilizado para deletar um atendimento dado o ID. Ele realizará um delete lógico, sem excluir fisicamente do banco de dados.
     *     Assim como inserir um atendimento atualiza o retorno de um cliente existente, o deletar atualiza o retorno também retirando o retorno do mesmo via
     *     {@link ClienteService#retiraRetornoCliente(String, String) ClienteService}
     * </p>
     * @param id ID do atendimento que será excluído.
     * @return {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o atendimento já esteja excluído - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o atendimento não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     * @see Transactional
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO deletarAtendimento(String id) {
        log.info("[SERVICE -> Deletar um atendimento] : AtendimentoService.deletarAtendimento(Linha: 89)");
        String tenantId = TenantContext.getTenantId();
        return atendimentoRepository.findByIdAndTenant(id, tenantId)
                .map(atendimento -> {
                    if (atendimento.status() == -1) {
                        log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.AtendimentoService.deletarAtendimento(String)");
                        throw new ConflictException(MESSAGE_EXCEPTION_EXCLUIDO);

                    }
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

    /**
     * <h2>
     *     Método para atualizar um atendimento.
     * </h2>
     * <h6>Apenas o administrador pode atualizar um atendimento.</h6>
     * <p>
     *     Este método é utilizado para atualizar um atendimento dado um ID. Ele receberá o ID e também os campos, via DTO, que serão atualizados.
     *     Esses campos são mapeados para entidade via {@link AtendimentoMapper#atualizarAtendimento(Atendimento, AtendimentoRequestDTO) Atendimento Mapper}.
     * </p>
     * @param id 'ID' do atendimento que será atualizado
     * @param atendimentoRequestDTO DTO que expõe os campos que poderão ser atualizados
     * @return {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o atendimento esteja excluído - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o atendimento não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     * @see Transactional
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public SuccessResponseDTO atualizarAtendimento(String id, AtendimentoRequestDTO atendimentoRequestDTO){
        log.info("[SERVICE -> Atualizar um atendimento] : AtendimentoService.atualizarAtendimento(Linha: 114)");
        return atendimentoRepository.findById(id, TenantContext.getTenantId())
                .map(atendimento -> {
                    if(atendimento.getStatus() == -1){
                        log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.AtendimentoService.atualizarAtendimento(String, AtendimentoRequestDTO)");
                        throw new ConflictException(MESSAGE_EXCEPTION_EXCLUIDO);

                    }
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

    /**
     * <h2>
     *     Método para ativar um atendimento.
     * </h2>
     * <h6>Apenas o administrador pode ativar um atendimento.</h6>
     * <p>
     *     Este método é utilizado para ativar um atendimento excluído. Assim como inserir um atendimento atualiza o retorno de um cliente existente e o deletar retira o retorno do mesmo, este método atualiza o retorno do cliente
     *     também.
     *     {@link ClienteService#atualizarRetornoDoCliente(String, String) ClienteService}
     * </p>
     * @param id 'ID' do atendimento que será ativado
     * @return {@link SuccessResponseDTO} e Status Code {@link HttpStatus#OK 200}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.ConflictException Caso o atendimento já esteja ativado - {@link HttpStatus#CONFLICT 409}.
     * @throws cloud.zenixapp.zenix.configs.exceptions.NotFoundException Caso o atendimento não seja encontrado - {@link HttpStatus#NOT_FOUND 404}.
     * @see Transactional
     * @see SuccessResponseDTO
     * @see ConflictException
     * @see NotFoundException
     */
    @Transactional
    public SuccessResponseDTO ativarAtendimento(String id){
        log.info("[SERVICE -> Ativar um atendimento] : AtendimentoService.ativarAtendimento(Linha: 139)");
        String tenantId = TenantContext.getTenantId();
        return atendimentoRepository.findByIdAndTenant(id, tenantId)
                .map(atendimento -> {
                    if(atendimento.status() == 1){
                        log.error("Exceção lançada pelo método: cloud.zenixapp.zenix.services.AtendimentoService.ativarAtendimento(String)");
                        throw new ConflictException("Atendimento já está ativo!");

                    }

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
