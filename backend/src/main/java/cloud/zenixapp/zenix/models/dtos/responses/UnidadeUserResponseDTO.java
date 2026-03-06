package cloud.zenixapp.zenix.models.dtos.responses;

import java.util.List;

public record UnidadeUserResponseDTO(
        Long id,
        String nomeUnidade,
        String endereco,
        Integer status,
        List<UsuarioSimplesResponseDTO> usuarios
) {
}
