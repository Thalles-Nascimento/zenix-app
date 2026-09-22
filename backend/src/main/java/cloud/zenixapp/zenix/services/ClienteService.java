package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ClienteExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.ClientePossuePlanoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UpdateErrorException;
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
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;


@Log4j2
@Service
public class ClienteService {

    // Mensagem padrão para exceções onde o objeto não foi encontrado.
    private static final String MESSAGE_EXCEPTION_NOT_FOUND = "Cliente não encontrado!";

    // Camada padrão para 'log'
    private static final String CAMADA = "SERVICE";

    // Entidade padrão para 'log'
    private static final String ENTITY_NAME = "Cliente";

    // Package padrão para 'log'
    private static final String LOGGER = "cloud.zenixapp.zenix.services.ClienteService";

    // Classe padrão para 'log'
    private static final String CLASS_NAME = "ClienteService";

    // TimeZone padrão para as funções de LocalDateTime.now().
    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

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
            HelpersLogs.logResponse(CAMADA, ENTITY_NAME, HttpStatus.CREATED, successResponseDTO.message(), inicio, fim);

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
        HelpersLogs.logResponse(CAMADA, ENTITY_NAME, HttpStatus.CREATED, successResponseDTO.message(), inicio, fim);

        return successResponseDTO;
    }

//  Lista os clientes pelo nome
    public ClientePlanosResumoResponseDTO clientePorNome(String nome){
        long inicio = System.currentTimeMillis();
        HelpersLogs.logInfoServices("Listar Cliente Pelo Nome", CLASS_NAME, "clientePorNome");

        Optional<ClientePlanosResumoResponseDTO> cliente = clienteRepository.findByName(nome, TenantContext.getTenantId());
        if (cliente.isPresent()){
            log.debug("Cliente encontrado: {}", true);

            long fim = System.currentTimeMillis();
            HelpersLogs.logResponse(CAMADA, "Cliente encontrado", HttpStatus.OK, "Cliente encontrado", inicio, fim);

            return cliente.get();
        }

        HelpersLogs.logException(LOGGER, "clientePorNome(String)");
        throw new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND);
    }

//  Lista os clientes por telefone - Endpoint para Fila
    public List<ClienteSimplesPlanosResponseDTO> clientesByTelefone(String numero) {
        return clienteRepository.findClientByNumber(numero, TenantContext.getTenantId());
    }


    @Transactional
    public void retiraRetornoCliente(String nome, String tenantId) {
        clienteRepository.findByName(nome, tenantId)
                .ifPresent(clientesDto -> {
                    if (clientesDto.status() != 1) throw new ClienteExcluidoException("Cliente foi excluído");
                    clienteRepository.retirarRetorno(clientesDto.id(), tenantId);
                });
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetarContadoresMensais() {
        int diaHoje = LocalDate.now(TIME_ZONE).getDayOfMonth();
        clienteRepository.resetarAtendimentosMes(diaHoje, TenantContext.getTenantId());
    }

//  Listar todos os clientes
    public List<ClientePlanosResumoResponseDTO> buscarTodosClientes() {
        return clienteRepository.findAll(TenantContext.getTenantId());
    }

    @Transactional
    public SuccessResponseDTO inserirPlano(String id, ClientePlanoRequestDTO requestDTO) throws NotFoundException {
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdAndTenant(id, tenantId)
                .map(cliente -> {
                    if(cliente.getPlanos() != null){
                        throw new ClientePossuePlanoException("Cliente possui um plano ativo");
                    }

                    cliente.setPlanos(planosService.buscarPlanoPorId(requestDTO.idPlano()));
                    cliente.setDataRenovacao(LocalDate.now(TIME_ZONE).plusMonths(1));


                    clienteRepository.save(cliente);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano ativado!"
                    );

                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    @Transactional
    public SuccessResponseDTO retirarPlano(String id){
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdAndTenant(id, tenantId)
                .map(cliente -> {

                    cliente.setPlanos(null);
                    cliente.setAtendimentosMes(0);
                    cliente.setDataRenovacao(null);

                    clienteRepository.save(cliente);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano retirado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    @Transactional
    public SuccessResponseDTO atualizarCliente(String id, ClienteUpdateRequestDTO clienteUpdateDTO){
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdAndTenant(id, tenantId)
                .map(cliente -> {
                    if(cliente.getStatus() == -1) {
                        throw new ClienteExcluidoException("Cliente foi excluído!");
                    }


                    clienteMapper.atualizarCliente(cliente, clienteUpdateDTO);

                    if (clienteUpdateDTO.telefoneCliente() != null && !clienteUpdateDTO.telefoneCliente().isBlank()) {
                        String numero = clienteUpdateDTO.telefoneCliente().replaceAll("\\D", "");
                        TelefoneCliente telefone = clienteRepository.findByTelefone_ClienteAndTenant(numero, tenantId)
                                .orElseGet(() -> {
                                    TelefoneCliente telefoneNovo = new TelefoneCliente(numero);
                                    telefoneNovo.setTenant(tenantId);
                                    return telefoneRepository.save(telefoneNovo);
                                });

                        cliente.setTelefoneCliente(telefone);
                    }

                    clienteRepository.save(cliente);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente atualizado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_EXCEPTION_NOT_FOUND));
    }

    @Transactional
    public SuccessResponseDTO deletarCliente(String id){
        String tenantId = TenantContext.getTenantId();
        int rowsAffected = clienteRepository.deleteLogico(id, LocalDateTime.now(TIME_ZONE), tenantId);
        if (rowsAffected == 1){
            return new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Cliente deletado com sucesso"
            );
        } else throw new UpdateErrorException("Não foi possível deletar!");

    }

    @Transactional
    public SuccessResponseDTO ativarCliente(String id){
        String tenantId = TenantContext.getTenantId();
        int rowsAffected = clienteRepository.ativarCliente(id, tenantId);
        if (rowsAffected == 1){
            return new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Cliente ativado com sucesso"
            );
        } else throw new UpdateErrorException("Não foi possível ativar!");
    }

    @Transactional
    public void atualizarRetornoDoCliente(String nome, String tenantId){
        clienteRepository.findByName(nome, tenantId)
                .ifPresent(clienteDTO -> {
                    if (clienteDTO.status() != 1) throw new ClienteExcluidoException("Cliente foi excluído");
                    if (clienteDTO.plano().id() != null) {
                        // Atendimento por plano
                        clienteRepository.atualizarAtendimentosMes(clienteDTO.id(), tenantId);

                    }

                    // Retorno do cliente para fins de relatório
                    clienteRepository.atualizarRetorno(clienteDTO.id(), tenantId);
                });
    }

}
