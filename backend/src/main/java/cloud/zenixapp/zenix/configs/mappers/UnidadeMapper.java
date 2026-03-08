package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UnidadeResponseDTO;
import cloud.zenixapp.zenix.models.entities.Unidades;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UnidadeMapper {


    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    Unidades toUnidade(UnidadeRequestDTO unidadeDTO);


    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    void atualizarUnidade(@MappingTarget Unidades unidade, UnidadeRequestDTO unidadeDTO);

    List<UnidadeResponseDTO> toListUnidadeDTO(List<Unidades> unidades);

    UnidadeResponseDTO toDTO(Unidades unidade);


}
