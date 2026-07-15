package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.PlanosRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.PlanosResponseDTO;
import cloud.zenixapp.zenix.models.entities.Planos;
import cloud.zenixapp.zenix.models.interfaces.PlanosView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlanosMapper {

    @Mapping(target = "clientes", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Planos toPlanos(PlanosRequestDTO planosRequestDTO);

    @Mapping(target = "clientes", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void atualizarPlano(@MappingTarget Planos planos, PlanosRequestDTO planosRequestDTO);

    @Mapping(target = "planoDescricao", source = "descricao")
    @Mapping(target = "limiteAtendimentos", source = "atendimentos")
    @Mapping(target = "clientes", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Planos fromPlanosViewtoPlanos(PlanosView planosView);

}
