package cloud.zenixapp.zenix.dtos;

import cloud.zenixapp.zenix.entities.enums.UsuariosRoleEnum;

public record UsuarioDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        UsuariosRoleEnum grupo
) {
}
