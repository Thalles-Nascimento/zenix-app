package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.enums.StatusFilaEnum;

import java.util.List;

public record SucessFilaResponseDTO(
        String id,
        String nomeCliente,
        List<String> servico,
        StatusFilaEnum status
) {
}
