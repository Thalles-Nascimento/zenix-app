package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;

import java.util.List;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        UsuariosRoleEnum grupo,
        int status,
        List<AtendimentoResponseDTO> atendimentos
) {
}
