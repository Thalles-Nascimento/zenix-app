package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AtendimentoMapper {

    @Mapping(target = "date", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    void atualizarAtendimento(@MappingTarget Atendimento atendimento, AtendimentoRequestDTO atendimentoRequestDTO);

    AtendimentoResponseDTO responseDTO(Atendimento atendimento);

    List<AtendimentoResponseDTO> listResponseDTO(List<Atendimento> atendimentosList);

}
