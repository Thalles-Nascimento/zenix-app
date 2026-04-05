package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;

public record UsuarioSimplesResponseDTO(
        String id,
        String nome,
        String cpf,
        String senha,
        String email,
        UnidadeResponseDTO unidade,
        UsuariosRoleEnum grupo,
        int status
) {

    public UsuarioSimplesResponseDTO(String id, String nome, String email, UsuariosRoleEnum grupo, int status) {
        this(id, nome, "", "", email, null, grupo, status);
    }
}
