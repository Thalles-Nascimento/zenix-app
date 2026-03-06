package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        Unidades unidade,
        UsuariosRoleEnum grupo,
        int status,
        List<AtendimentoResponseDTO> atendimentos
) {
}
