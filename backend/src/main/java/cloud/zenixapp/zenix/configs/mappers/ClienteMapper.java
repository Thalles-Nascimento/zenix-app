package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.responses.ClienteSimplesResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClienteMapper {

    List<ClienteSimplesResponseDTO> listResponseDTO(List<Clientes> clientesList);

}
