package cloud.zenixapp.zenix.models.dtos;

import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;

public record UsuarioDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        UsuariosRoleEnum grupo
) {
}
