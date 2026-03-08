package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.ClienteMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.ClienteRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ClienteSimplesResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessClienteResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SucessAtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.ClienteRepository;
import cloud.zenixapp.zenix.repositories.TelefoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TelefoneRepository telefoneRepository;

    @Autowired
    private ClienteMapper clienteMapper;


    @Transactional
    public SuccessClienteResponseDTO save(ClienteRequestDTO clienteDTO){
        Clientes cliente = new Clientes();
        cliente.setNomeCliente(clienteDTO.nomeCliente());
        Optional<TelefoneCliente> telefone = telefoneRepository.findByNumber(clienteDTO.telefoneCliente());

        if (telefone.isPresent()){
            cliente.setTelefoneCliente(telefone.get());
            clienteRepository.save(cliente);
            return new SuccessClienteResponseDTO(
                    HttpStatus.CREATED.value(),
                    "Cliente inserido com Sucesso"
            );
        }
        TelefoneCliente newTelefone = telefoneRepository.save(new TelefoneCliente(clienteDTO.telefoneCliente()));
        cliente.setTelefoneCliente(newTelefone);
        clienteRepository.save(cliente);

        return new SuccessClienteResponseDTO(
                HttpStatus.CREATED.value(),
                "Cliente inserido com Sucesso"
        );
    }

    public List<ClienteSimplesResponseDTO> clientesByTelefone(String numero) {
        Optional<TelefoneCliente> telefone = telefoneRepository.findByNumber(numero);
        if (telefone.isEmpty()) {
            return Collections.emptyList();
        }
        return clienteMapper.listResponseDTO(clienteRepository.findClientByNumber(telefone.get().getId()));
    }

    @Transactional
    public SuccessClienteResponseDTO atualizarRetornoCliente(Long id) throws NotFoundException {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    int count = cliente.getTotalRetornos();
                    System.out.println(count);
                    count = count + 1;
                    cliente.setTotalRetornos(count);

                    clienteRepository.save(cliente);
                    System.out.println(count);
                    return new SuccessClienteResponseDTO(
                            HttpStatus.OK.value(),
                            "Obrigado pelo retorno!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }


}
