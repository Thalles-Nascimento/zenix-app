package cloud.zenixapp.test.zenix.configs.mappers;

import cloud.zenixapp.test.zenix.dtos.AtendimentoRequestDTO;
import cloud.zenixapp.test.zenix.dtos.AtendimentoResponseDTO;
import cloud.zenixapp.test.zenix.entities.Atendimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AtendimentoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    void atualizarAtendimento(@MappingTarget Atendimento atendimento, AtendimentoRequestDTO atendimentoRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Atendimento insertAtendimento(AtendimentoRequestDTO atendimentoRequestDTO);

    AtendimentoResponseDTO responseDTO(Atendimento atendimento);

    List<AtendimentoResponseDTO> listResponseDTO(List<Atendimento> atendimentosList);

}
