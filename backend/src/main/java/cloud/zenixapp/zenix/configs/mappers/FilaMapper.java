package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.responses.FilaResponseDTO;
import cloud.zenixapp.zenix.models.entities.Fila;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FilaMapper {
    List<FilaResponseDTO> toListFilaDTO(List<Fila> filas);

}
