package cloud.zenixapp.zenix.models.dtos.responses;

import java.time.LocalTime;

public record FilaResponseDTO(
        String nomeCliente,

        String servico,

        String formaPagamento,

        LocalTime horario

) {
}
