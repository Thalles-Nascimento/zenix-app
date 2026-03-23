package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.ClienteUpdateRequestDTO;
import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ClienteResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.Unidades;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClienteMapper {

    @IterableMapping(elementTargetType = ClienteResponseDTO.class)
    List<ClienteResponseDTO> listResponseDTO(List<Clientes> clientesList);

    @Mapping(target = "telefone", source = "telefoneCliente.telefoneCliente")
    @Mapping(target = "vezesRetorno", source = "totalRetornos")
    @Mapping(target = "plano", source = "planos")
    ClienteResponseDTO toClienteResponseDTO(Clientes cliente);

    @Mapping(target = "totalRetornos", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "planos", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataRenovacao", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "atendimentosMes", ignore = true)
    @Mapping(target = "telefoneCliente", ignore = true)
    void atualizarCliente(@MappingTarget Clientes clientes, ClienteUpdateRequestDTO clienteUpdateRequestDTO);

}
