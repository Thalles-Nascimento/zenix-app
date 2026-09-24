package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ConflictException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.ClienteMapper;
import cloud.zenixapp.zenix.configs.utils.HelpersLogs;
import cloud.zenixapp.zenix.models.dtos.requests.ClientePlanoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClientePlanosResumoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClienteSimplesPlanosResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import cloud.zenixapp.zenix.repositories.ClienteRepository;
import cloud.zenixapp.zenix.repositories.TelefoneRepository;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static cloud.zenixapp.zenix.configs.utils.HelpersVar.*;


@Log4j2
@Service
public class ClienteService {

    // Entidade padrão para 'log'
    private static final String ENTITY_NAME = "Cliente";

    private static final String CLIENTE_FOUND = "Cliente encontrado";

    // Package padrão para 'log'
    private static final String LOGGER = "cloud.zenixapp.zenix.services.ClienteService";

    // Classe padrão para 'log'
    private static final String CLASS_NAME = "ClienteService";

    private static final String ROWS_AFECTED = "Linhas afetadas: %s";

    private final ClienteRepository clienteRepository;
    private final TelefoneRepository telefoneRepository;
    private final ClienteMapper clienteMapper;
    private final PlanosService planosService;

    public ClienteService(ClienteRepository clienteRepository, TelefoneRepository telefoneRepository, ClienteMapper clienteMapper, PlanosService planosService) {
        this.clienteRepository = clienteRepository;
        this.telefoneRepository = telefoneRepository;
        this.clienteMapper = clienteMapper;
        this.planosService = planosService;
    }

    @Transactional
    public SuccessResponseDTO save(@NonNull ClienteRequestDTO clienteDTO){
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Inserir Cliente", CLASS_NAME, "save");

        String tenantId = TenantContext.getTenantId();

        Clientes cliente = new Clientes();
        cliente.setNomeCliente(clienteDTO.nomeCliente());
        cliente.setTenant(tenantId);

        log.debug("Buscando telefone no banco de dados...");
        Optional<TelefoneCliente> telefone = clienteRepository.findByTelefone_ClienteAndTenant(clienteDTO.telefoneCliente(), tenantId);
        log.debug("Telefone encontrado? {}", telefone.isPresent());

        if (telefone.isPresent()){
            cliente.setTelefoneCliente(telefone.get());
            clienteRepository.save(cliente);

            SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(
                    HttpStatus.CREATED.value(),
                    "Cliente inserido com Sucesso"
            );
            long fim = System.currentTimeMillis();
            HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.CREATED, successResponseDTO.message(), inicio, fim);

            return successResponseDTO;
        }
        log.debug("Criando Telefone...");
        TelefoneCliente telefoneNovo = new TelefoneCliente();
        telefoneNovo.setTelefoneCliente(clienteDTO.telefoneCliente());
        telefoneNovo.setTenant(tenantId);

        cliente.setTelefoneCliente(telefoneRepository.save(telefoneNovo));
        log.debug("Telefone criado!");
        clienteRepository.save(cliente);

        SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Cliente inserido com Sucesso"
        );
        long fim = System.currentTimeMillis();
        HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.CREATED, successResponseDTO.message(), inicio, fim);

        return successResponseDTO;
    }

//  Lista os clientes pelo nome
    public ClientePlanosResumoResponseDTO clientePorNome(String nome){
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Listar Cliente Pelo Nome", CLASS_NAME, "clientePorNome");

        Optional<ClientePlanosResumoResponseDTO> cliente = clienteRepository.findByName(nome, TenantContext.getTenantId());
        log.debug("Cliente encontrado? {}", cliente.isPresent());
        if (cliente.isPresent()){
            log.debug("Cliente nome: {}", cliente.get().nomeCliente());
            long fim = System.currentTimeMillis();
            HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, CLIENTE_FOUND, inicio, fim);

            return cliente.get();
        }

        HelpersLogs.logException(LOGGER, "clientePorNome(String)");
        throw new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND);
    }

//  Lista os clientes por telefone - Endpoint para Fila
    public List<ClienteSimplesPlanosResponseDTO> clientesByTelefone(String numero) {
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Listar Cliente Pelo Número de Telefone", CLASS_NAME, "clientesByTelefone");

        List<ClienteSimplesPlanosResponseDTO> response = clienteRepository.findClientByNumber(numero, TenantContext.getTenantId());

        long fim = System.currentTimeMillis();
        HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, CLIENTE_FOUND, inicio, fim);

        return response;
    }


    //  Listar todos os clientes
    public List<ClientePlanosResumoResponseDTO> buscarTodosClientes() {
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Listar Todos os Clientes", CLASS_NAME, "buscarTodosClientes");

        List<ClientePlanosResumoResponseDTO> response = clienteRepository.findAll(TenantContext.getTenantId());

        long fim = System.currentTimeMillis();
        HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, CLIENTE_FOUND, inicio, fim);

        return response;
    }

    @Transactional
    public void retiraRetornoCliente(String nome, String tenantId) {
        HelpersLogs.logInfoServices("Retirar retorno do Cliente", CLASS_NAME, "retiraRetornoCliente");

        clienteRepository.findByName(nome, tenantId)
                .ifPresent(clientesDto -> {
                    if (clientesDto.status() != 1) {
                        log.warn("Cliente foi excluído");
                        log.debug("O cliente já foi excluído: Nome => {}", clientesDto.nomeCliente());

                    } else {
                        log.debug("Retirando o retorno...");
                        clienteRepository.retirarRetorno(clientesDto.id(), tenantId);

                        log.info("[{}:{}]: [Message: {}]", CAMADA_SERVICE, ENTITY_NAME, "Retorno atualizado");

                    }
                });
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetarContadoresMensais() {
        HelpersLogs.logInfoServices("Resetar contador do plano", CLASS_NAME, "resetarContadoresMensais");
        int diaHoje = LocalDate.now(TIME_ZONE).getDayOfMonth();

        log.debug("Resetando contador...");
        clienteRepository.resetarAtendimentosMes(diaHoje, TenantContext.getTenantId());
        HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, null, "Contador resetado");

    }

    @Transactional
    public SuccessResponseDTO inserirPlano(String id, ClientePlanoRequestDTO requestDTO) throws NotFoundException {
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Inserir um plano", CLASS_NAME, "inserirPlano");

        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdAndTenant(id, tenantId)
                .map(cliente -> {
                    if(cliente.getPlanos() != null){
                        HelpersLogs.logException(LOGGER, "inserirPlano");
                        log.debug("Cliente possui um plano ativo => {}", cliente.getPlanos());
                        throw new ConflictException("Cliente possui um plano ativo");

                    }

                    log.debug("Associando um plano...");
                    cliente.setPlanos(planosService.buscarPlanoPorId(requestDTO.idPlano()));
                    cliente.setDataRenovacao(LocalDate.now(TIME_ZONE).plusMonths(1));
                    log.debug("Plano associado.");


                    clienteRepository.save(cliente);

                    SuccessResponseDTO response = new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano Ativado"
                    );

                    long fim = System.currentTimeMillis();
                    HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, response.message(), inicio, fim);

                    return response;

                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    @Transactional
    public SuccessResponseDTO retirarPlano(String id){
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Retirar um plano", CLASS_NAME, "retirarPlano");

        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdAndTenant(id, tenantId)
                .map(cliente -> {
                    if(cliente.getPlanos() != null) {
                        HelpersLogs.logException(LOGGER, "retirarPlano");
                        throw new ConflictException("Cliente não possui plano!");

                    }

                    log.debug("Retirando o plano...");
                    cliente.setPlanos(null);
                    cliente.setAtendimentosMes(0);
                    cliente.setDataRenovacao(null);
                    log.debug("Plano retirado.");

                    clienteRepository.save(cliente);

                    SuccessResponseDTO response = new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano Retirado"
                    );

                    long fim = System.currentTimeMillis();
                    HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, response.message(), inicio, fim);

                    return response;

                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    @Transactional
    public SuccessResponseDTO atualizarCliente(String id, ClienteUpdateRequestDTO clienteUpdateDTO){
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Atualizar um Cliente", CLASS_NAME, "atualizarCliente");

        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdAndTenant(id, tenantId)
                .map(cliente -> {
                    if(cliente.getStatus() == -1) {
                        HelpersLogs.logException(LOGGER, "atualizarCliente");
                        throw new ConflictException("Cliente foi excluído!");

                    }

                    log.debug("Atualizando o cliente");
                    clienteMapper.atualizarCliente(cliente, clienteUpdateDTO);

                    if (clienteUpdateDTO.telefoneCliente() != null && !clienteUpdateDTO.telefoneCliente().isBlank()) {
                        log.debug("Associando um telefone ao cliente...");
                        String numero = clienteUpdateDTO.telefoneCliente().replaceAll("\\D", "");
                        log.debug("Telefone => {}", numero);
                        TelefoneCliente telefone = clienteRepository.findByTelefone_ClienteAndTenant(numero, tenantId)
                                .orElseGet(() -> {
                                    log.debug("Telefone não existe no banco de dados.");
                                    TelefoneCliente telefoneNovo = new TelefoneCliente(numero);
                                    telefoneNovo.setTenant(tenantId);
                                    return telefoneRepository.save(telefoneNovo);
                                });

                        cliente.setTelefoneCliente(telefone);
                        log.debug("Telefone associado!");
                    }

                    clienteRepository.save(cliente);

                    SuccessResponseDTO response = new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente atualizado!"
                    );

                    long fim = System.currentTimeMillis();
                    HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, response.message(), inicio, fim);

                    return response;

                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    @Transactional
    public SuccessResponseDTO deletarCliente(String id){
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Deletar um Cliente", CLASS_NAME, "deletarCliente");

        int rowsAffected = clienteRepository.deleteLogico(id, LocalDateTime.now(TIME_ZONE), TenantContext.getTenantId());
        String mensagem = ROWS_AFECTED.formatted(rowsAffected);
        log.debug(mensagem);

        if (rowsAffected == 1){
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Cliente Deletado!"
            );

            long fim = System.currentTimeMillis();
            HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, response.message(), inicio, fim);

            return response;

        } else {
            HelpersLogs.logException(LOGGER, "deletarCliente");
            throw new ConflictException("Não foi possível deletar o cliente!");

        }

    }

    @Transactional
    public SuccessResponseDTO ativarCliente(String id){
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Ativar um Cliente", CLASS_NAME, "ativarCliente");

        int rowsAffected = clienteRepository.ativarCliente(id, TenantContext.getTenantId());
        String mensagem = ROWS_AFECTED.formatted(rowsAffected);
        log.debug(mensagem);

        if (rowsAffected == 1){
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Cliente Ativado!"
            );

            long fim = System.currentTimeMillis();
            HelpersLogs.logResponse(CAMADA_SERVICE, ENTITY_NAME, HttpStatus.OK, response.message(), inicio, fim);

            return response;

        } else {
            HelpersLogs.logException(LOGGER, "ativarCliente");
            throw new ConflictException("Não foi possível ativar o cliente!");

        }
    }

    @Transactional
    public void atualizarRetornoDoCliente(String nome, String tenantId){
        HelpersLogs.logInfoServices("Atualizar retorno do Cliente", CLASS_NAME, "atualizarRetornoDoCliente");

        clienteRepository.findByName(nome, tenantId)
                .ifPresent(clienteDTO -> {
                    if (clienteDTO.status() != 1) {
                        HelpersLogs.logException(LOGGER, "atualizarRetornoDoCliente");
                        throw new ConflictException("Cliente foi excluído");

                    }

                    if (clienteDTO.plano().id() != null) {
                        log.debug("Atualizando atendimento do mês por plano...");
                        // Atendimento por plano
                        clienteRepository.atualizarAtendimentosMes(clienteDTO.id(), tenantId);
                        log.debug("Atendimento do mês atualizado.");

                    }

                    log.debug("Atualizando retorno...");
                    // Retorno do cliente para fins de relatório
                    clienteRepository.atualizarRetorno(clienteDTO.id(), tenantId);
                    log.debug("Retorno atualizado.");

                });
    }

}
