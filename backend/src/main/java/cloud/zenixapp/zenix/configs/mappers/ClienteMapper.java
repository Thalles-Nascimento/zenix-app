package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ClienteResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.interfaces.ClientesProjectionView;
import cloud.zenixapp.zenix.models.interfaces.ClientesSimplesProjectionView;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClienteMapper {

    @IterableMapping(elementTargetType = ClienteResponseDTO.class)
    List<ClienteResponseDTO> listResponseDTO(List<Clientes> clientesList);

    @Mapping(target = "totalRetornos", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "planos", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataRenovacao", ignore = true)
    @Mapping(target = "atendimentosMes", ignore = true)
    @Mapping(target = "telefoneCliente", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void atualizarCliente(@MappingTarget Clientes clientes, ClienteUpdateRequestDTO clienteUpdateRequestDTO);

    @Mapping(target = "telefoneCliente", ignore = true)
    @Mapping(target = "totalRetornos", source = "retorno")
    @Mapping(target = "planos", ignore = true)
    @Mapping(target = "nomeCliente", source = "nome")
    @Mapping(target = "atendimentosMes", source = "atendimentoMes")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Clientes toClientes(ClientesProjectionView clientesView);

    @Mapping(target = "totalRetornos", ignore = true)
    @Mapping(target = "planos", ignore = true)
    @Mapping(target = "dataRenovacao", ignore = true)
    @Mapping(target = "atendimentosMes", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "telefoneCliente", ignore = true)
    @Mapping(target = "nomeCliente", source = "nome")
    @Mapping(target = "updatedAt", source = "updatedAt")
    Clientes toClienteSimples(ClientesSimplesProjectionView clientesSimplesProjectionView);
}
