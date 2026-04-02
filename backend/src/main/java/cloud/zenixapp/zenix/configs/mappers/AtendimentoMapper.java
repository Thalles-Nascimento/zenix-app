package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AtendimentoMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "observacao", source = "observacao")
    void atualizarAtendimento(@MappingTarget Atendimento atendimento, AtendimentoRequestDTO atendimentoRequestDTO);

    AtendimentoResponseDTO responseDTO(Atendimento atendimento);

    List<AtendimentoResponseDTO> listResponseDTO(List<Atendimento> atendimentosList);

}
