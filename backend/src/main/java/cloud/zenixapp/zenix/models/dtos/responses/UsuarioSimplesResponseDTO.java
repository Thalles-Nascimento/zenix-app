package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;

import java.util.List;

public record UsuarioSimplesResponseDTO(
        Long id,
        String nome,
        String email,
        UsuariosRoleEnum grupo,
        int status
) {
}
