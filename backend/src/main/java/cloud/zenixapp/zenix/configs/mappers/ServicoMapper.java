package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.ServicoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.ServicoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Servicos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServicoMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Servicos toServicos(ServicoRequestDTO servicoRequestDTO);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void atualizarServico(@MappingTarget Servicos servico, ServicoRequestDTO servicoRequestDTO);

    List<ServicoResponseDTO> toListServicos(List<Servicos> servicosList);

    ServicoResponseDTO toServicoDTO(Servicos servicos);

}
