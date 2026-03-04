package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.enums.StatusFilaEnum;

import java.time.LocalTime;

public record FilaResponseDTO(
        Long id,

        String nomeCliente,

        String servico,

        String formaPagamento,

        LocalTime horario,

        StatusFilaEnum status

) {
}
