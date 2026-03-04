package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.enums.StatusFilaEnum;

public record SucessFilaResponseDTO(
        Long id,
        String nomeCliente,
        String servico,
        StatusFilaEnum status
) {
}
