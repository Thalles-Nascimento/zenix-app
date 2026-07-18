package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.enums.StatusFilaEnum;

import java.time.LocalTime;
import java.util.List;

public record FilaResponseDTO(
        String id,

        String nomeCliente,

        List<String> servico,

        String formaPagamento,

        LocalTime horario,

        boolean semPreferencia,

        StatusFilaEnum status

) {
}
