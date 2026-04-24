package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ClienteResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UnidadeResponseDTO;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.interfaces.UnidadeSimplesView;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UnidadeMapper {


    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    Unidades toUnidade(UnidadeRequestDTO unidadeDTO);

    @Mapping(target = "nomeUnidade", source = "nome")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    Unidades fromUnidadesViewToUnidades(UnidadeSimplesView unidadeView);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    void atualizarUnidade(@MappingTarget Unidades unidade, UnidadeRequestDTO unidadeDTO);

    List<UnidadeResponseDTO> toListUnidadeDTO(List<Unidades> unidades);

    UnidadeResponseDTO toDTO(Unidades unidade);


}
