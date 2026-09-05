package cloud.zenixapp.zenix.models.dtos.responses.usuarios;

import cloud.zenixapp.zenix.models.dtos.responses.unidades.UnidadeResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;

import java.util.List;

public record UsuarioResponseDTO(
        String id,
        String nome,
        String email,
        String cpf,
        UnidadeResponseDTO unidade,
        UsuariosRoleEnum grupo,
        int status,
        List<AtendimentoResponseDTO> atendimentos
) {
}
