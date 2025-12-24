package cloud.zenixapp.test.zenix.configs.mappers;

import cloud.zenixapp.test.zenix.dtos.AtendimentoDTO;
import cloud.zenixapp.test.zenix.entities.Atendimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AtendimentoMapper {

    @Mapping(target = "id", ignore = true)
    void updateFromDTO(@MappingTarget Atendimento atendimento, AtendimentoDTO atendimentoDTO);

//    @Mapping(target = "id", ignore = true)
    @Mapping(source = "descricao", target = "descricao")
    @Mapping(source = "servico", target = "servico")
    @Mapping(source = "valor", target = "valor")
    Atendimento insertAtendimento(AtendimentoDTO atendimentoDTO);

}
