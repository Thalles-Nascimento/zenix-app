package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.entities.TelefoneCliente;

public record ClienteSimplesResponseDTO(
        Long id,
        String nomeCliente,
        int vezesRetorno
) {
}
