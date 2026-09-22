package cloud.zenixapp.zenix.models.dtos.responses.unidades;

import cloud.zenixapp.zenix.models.dtos.responses.usuarios.UsuarioSimplesResponseDTO;

import java.util.List;

public record UnidadeUserResponseDTO(
        String id,
        String nomeUnidade,
        String endereco,
        Integer status,
        List<UsuarioSimplesResponseDTO> usuarios
) {
}
