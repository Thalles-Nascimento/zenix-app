package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.PlanosRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.PlanosResponseDTO;
import cloud.zenixapp.zenix.models.entities.Planos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlanosMapper {

    @Mapping(target = "clientes", ignore = true)
    @Mapping(target = "update_at", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    Planos toPlanos(PlanosRequestDTO planosRequestDTO);

    @Mapping(target = "clientes", ignore = true)
    @Mapping(target = "update_at", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    void atualizarPlano(@MappingTarget Planos planos, PlanosRequestDTO planosRequestDTO);

    List<PlanosResponseDTO> toListPlanosDTO(List<Planos> planosList);

    PlanosResponseDTO toPlanosDTO(Planos planos);

}
