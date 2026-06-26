package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ClienteExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.ClientePossuePlanoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.ClienteMapper;
import cloud.zenixapp.zenix.models.dtos.requests.ClientePlanoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ClienteResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.Planos;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.interfaces.ClientesProjectionView;
import cloud.zenixapp.zenix.repositories.ClienteRepository;
import cloud.zenixapp.zenix.repositories.TelefoneRepository;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

//   TODO Criar um service comum entre Telefone e Clientes
    @Autowired
    private TelefoneRepository telefoneRepository;

    @Autowired
    private ClienteMapper clienteMapper;

    @Autowired
    private PlanosService planosService;

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public SuccessResponseDTO save(ClienteRequestDTO clienteDTO){
        String tenantId = TenantContext.getTenantId();
        Tenants tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant não encontrado"));

        Clientes cliente = new Clientes();
        cliente.setNomeCliente(clienteDTO.nomeCliente());
        cliente.setTenant(tenant);

        Optional<TelefoneCliente> telefone = clienteRepository.findByNumber(clienteDTO.telefoneCliente(), tenantId);

        if (telefone.isPresent()){
            cliente.setTelefoneCliente(telefone.get());
            clienteRepository.save(cliente);

            return new SuccessResponseDTO(
                    HttpStatus.CREATED.value(),
                    "Cliente inserido com Sucesso"
            );
        }
        TelefoneCliente telefoneNovo = new TelefoneCliente();
        telefoneNovo.setTelefoneCliente(clienteDTO.telefoneCliente());
        telefoneNovo.setTenant(tenant);

        cliente.setTelefoneCliente(telefoneRepository.save(telefoneNovo));
        clienteRepository.save(cliente);

        return new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Cliente inserido com Sucesso"
        );
    }

    public List<ClientesProjectionView> clientesByTelefone(String numero) {
        return clienteRepository.findClientByNumber(numero, TenantContext.getTenantId());
    }

    @Transactional
    public SuccessResponseDTO atualizarRetornoCliente(String id) {
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findById(id, tenantId)
                .map(clienteView -> {
                    int count = clienteView.getRetorno();
                    count = count + 1;
                    Clientes cliente = clienteMapper.toClientes(clienteView);

                    cliente.setTotalRetornos(count);

                    clienteRepository.save(cliente);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Obrigado pelo retorno!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    @Transactional
    public String retiraRetornoCliente(String nome, String tenantId) {
        Optional<ClientesProjectionView> clienteView = clienteRepository.findByName(nome, tenantId);

        Clientes cliente = clienteMapper.toClientes(clienteView.get());

        int count = clienteView.get().getRetorno();
        if (count == 0){
            return "Cliente retirado";
        }
        count = count - 1;
        cliente.setTotalRetornos(count);

        clienteRepository.save(cliente);

        return "Cliente retirado";

    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetarContadoresMensais() {
        int diaHoje = LocalDate.now().getDayOfMonth();
        clienteRepository.resetarAtendimentosMes(diaHoje, TenantContext.getTenantId());
    }

    public List<ClientesProjectionView> buscarTodosClientes() {
        return clienteRepository.findAll(TenantContext.getTenantId());
    }

    @Transactional
    public SuccessResponseDTO inserirPlano(String id, ClientePlanoRequestDTO requestDTO) throws NotFoundException {
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findById(id, tenantId)
                .map(clientesView -> {
                    if(clientesView.getPlanosId() != null){
                        throw new ClientePossuePlanoException("Cliente possui um plano ativo");
                    }

                    Planos plano = planosService.buscarPlanoPorId(requestDTO.idPlano());
                    Clientes cliente = clienteMapper.toClientes(clientesView);

                    clienteRepository.findByNumber(clientesView.getTelefone(), tenantId)
                            .ifPresent(cliente::setTelefoneCliente);

                    cliente.setPlanos(plano);
                    cliente.setDataRenovacao(LocalDate.now().plusMonths(1));


                    clienteRepository.save(cliente);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano ativado!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    @Transactional
    public SuccessResponseDTO retirarPlano(String id){
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findById(id, tenantId)
                .map(clienteView -> {

                    Clientes cliente = clienteMapper.toClientes(clienteView);

                    clienteRepository.findByNumber(clienteView.getTelefone(), tenantId)
                            .ifPresent(cliente::setTelefoneCliente);

                    cliente.setPlanos(null);
                    cliente.setAtendimentosMes(0);
                    cliente.setDataRenovacao(null);

                    clienteRepository.save(cliente);
                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Plano retirado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    @Transactional
    public SuccessResponseDTO atualizarCliente(String id, ClienteUpdateRequestDTO clienteUpdateDTO){
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdSimples(id, tenantId)
                .map(clienteView -> {
                    if(clienteView.getStatus() == -1) {
                        throw new ClienteExcluidoException("Cliente já foi excluído!");
                    }

                    Clientes cliente = clienteMapper.toClienteSimples(clienteView);

                    clienteRepository.findByNumber(clienteView.getTelefone(), tenantId)
                            .ifPresent(cliente::setTelefoneCliente);

                    clienteMapper.atualizarCliente(cliente, clienteUpdateDTO);

                    if (clienteUpdateDTO.telefoneCliente() != null && !clienteUpdateDTO.telefoneCliente().isBlank()) {
                        String numero = clienteUpdateDTO.telefoneCliente().replaceAll("\\D", "");
                        TelefoneCliente telefone = clienteRepository.findByNumber(numero, tenantId)
                                .orElseGet(() -> {
                                    TelefoneCliente telefoneNovo = new TelefoneCliente(numero);
                                    telefoneNovo.setTenant(tenantRepository.findById(tenantId).get());
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
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    @Transactional
    public SuccessResponseDTO deletarCliente(String id){
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdSimples(id, tenantId)
                .map(cliente -> {
                    if(cliente.getStatus() == -1) {
                        throw new ClienteExcluidoException("Cliente já foi excluído!");
                    }
                    clienteRepository.deleteLogico(cliente.getId(), LocalDateTime.now(),tenantId);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente deletado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    @Transactional
    public SuccessResponseDTO ativarCliente(String id){
        String tenantId = TenantContext.getTenantId();
        return clienteRepository.findByIdSimples(id, tenantId)
                .map(cliente -> {
                    if (cliente.getStatus() != -1){
                        throw new RuntimeException("Cliente já está ativo");
                    }

                    clienteRepository.ativarCliente(cliente.getId(), tenantId);
                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente ativado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    public void atualizarAtendimentosMes(String nome, String tenantId){
        Optional<ClientesProjectionView> clienteView = clienteRepository.findByName(nome, tenantId);

        if (clienteView.isEmpty() || clienteView.get().getPlanosId() == null) return;

        Clientes cliente = clienteMapper.toClientes(clienteView.get());
        cliente.setAtendimentosMes(clienteView.get().getAtendimentoMes() + 1);

        Planos plano = planosService.buscarPlanoPorId(clienteView.get().getPlanosId());
        cliente.setPlanos(plano);

        clienteRepository.save(cliente);
    }

    public List<ClientesProjectionView> buscarClientePorNome(String nome){
        return clienteRepository.findByNameContaining(nome, TenantContext.getTenantId());
    }

}
