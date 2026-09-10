package cloud.zenixapp.zenix.models.dtos.responses.clientes;

import java.time.LocalDateTime;

public record ClienteSimplesResponseDTO(
        String id,
        String nomeCliente,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        int status
) {
}
