package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UsuarioResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UsuarioResponseSimplesDTO;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {


    @Mapping(target = "atendimentos", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "filaClientes", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "unidade", ignore = true)
    void atualizarUsuario(@MappingTarget Usuarios user, UsuarioRequestDTO usuarioRequestDTO);


    UsuarioResponseDTO usuarioResponseDTO(Usuarios user);

    List<UsuarioResponseDTO> listResponseDTO(List<Usuarios> usuariosList);

    List<UsuarioResponseSimplesDTO> listResponseSimplesDTO(List<Usuarios> usuariosList);

}
