package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.ClienteExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.ClientePossuePlanoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.ClienteMapper;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ClienteResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.Planos;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.repositories.ClienteRepository;
import cloud.zenixapp.zenix.repositories.TelefoneRepository;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
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


//    TODO Validar save
    @Transactional
    public SuccessResponseDTO save(ClienteRequestDTO clienteDTO){
        Tenants tenant = tenantRepository.findById(TenantContext.getTenantId())
                .orElseThrow(() -> new NotFoundException("Tenant não encontrado"));

        Clientes cliente = new Clientes();
        cliente.setNomeCliente(clienteDTO.nomeCliente());
        cliente.setTenant(tenant);

        Optional<TelefoneCliente> telefone = clienteRepository.findByNumber(clienteDTO.telefoneCliente());

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

    public List<ClienteResponseDTO> clientesByTelefone(String numero) {
        Optional<TelefoneCliente> telefone = clienteRepository.findByNumber(numero);
//        TODO Validar retorno
        if (telefone.isEmpty()) {
            return Collections.emptyList();
        }
        return clienteMapper.listResponseDTO(clienteRepository.findClientByNumber(telefone.get().getId()));
    }

    @Transactional
    public SuccessResponseDTO atualizarRetornoCliente(String id) throws NotFoundException {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    int count = cliente.getTotalRetornos();
                    count = count + 1;
                    cliente.setTotalRetornos(count);

                    clienteRepository.save(cliente);
                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Obrigado pelo retorno!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

//    TODO Refatorar a Fila para que tenha relacionamento com o Cliente
    @Transactional
    public String retiraRetornoCliente(String nome) {
        Clientes cliente = clienteRepository.findByName(nome);

        int count = cliente.getTotalRetornos();
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
        clienteRepository.resetarAtendimentosMes(diaHoje);
    }

    public List<ClienteResponseDTO> buscarTodosClientes() {
        return clienteMapper.listResponseDTO(clienteRepository.findAll());
    }

    @Transactional
    public SuccessResponseDTO inserirPlano(String id, String idPlano) throws NotFoundException {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    if(cliente.getAtendimentosMes() > 0){
                        throw new ClientePossuePlanoException("Cliente possui um plano ativo");
                    }
                    Planos plano = planosService.buscarPlanoPorId(idPlano);
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
        return clienteRepository.findById(id)
                .map(cliente -> {
                    if(cliente.getStatus() == -1){
                        throw new ClienteExcluidoException("Cliente foi excluído!");

                    }

                    cliente.setPlanos(null);
                    cliente.setAtendimentosMes(0);
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
        return clienteRepository.findById(id)
                .map(cliente -> {
                    if(cliente.getStatus() == -1){
                        throw new ClienteExcluidoException("Cliente foi excluído!");
                    }

                    clienteMapper.atualizarCliente(cliente, clienteUpdateDTO);

                    if (clienteUpdateDTO.telefoneCliente() != null && !clienteUpdateDTO.telefoneCliente().isBlank()) {
                        String numero = clienteUpdateDTO.telefoneCliente().replaceAll("\\D", "");
                        Optional<TelefoneCliente> telefone = clienteRepository.findByNumber(numero);
                        if (telefone.isPresent()) {
                            cliente.setTelefoneCliente(telefone.get());
                        } else {
                            TelefoneCliente newTelefone = telefoneRepository.save(new TelefoneCliente(numero));
                            cliente.setTelefoneCliente(newTelefone);
                        }
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
        return clienteRepository.findById(id)
                .map(cliente -> {
                    if(cliente.getStatus() == -1){
                        throw new ClienteExcluidoException("Cliente já foi excluído!");

                    }

                    clienteRepository.deleteLogico(id);
                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente deletado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    @Transactional
    public SuccessResponseDTO ativarCliente(String id){
        return clienteRepository.findById(id)
                .map(cliente -> {
                    if(cliente.getStatus() != -1){
                        throw new ClienteExcluidoException("Cliente já está ativo!");
                    }

                    clienteRepository.ativarCliente(id);
                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Cliente ativado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    public void atualizarAtendimentosMes(String nome){
        Clientes cliente = clienteRepository.findByName(nome);
        if (cliente == null || cliente.getPlanos() == null) return;
        cliente.setAtendimentosMes(cliente.getAtendimentosMes() + 1);
        clienteRepository.save(cliente);
    }

    public List<ClienteResponseDTO> buscarClientePorNome(String nome){
        return clienteMapper.listResponseDTO(clienteRepository.findByNameContaining(nome));
    }

}
