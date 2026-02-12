package cloud.zenixapp.zenix.models.dtos;

import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;

public record UsuarioResponseDTO(
        String nome,
        String email,
        String cpf,
        UsuariosRoleEnum grupo
) {
}
