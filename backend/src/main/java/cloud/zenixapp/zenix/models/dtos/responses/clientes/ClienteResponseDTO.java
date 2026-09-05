package cloud.zenixapp.zenix.models.dtos.responses.clientes;
import cloud.zenixapp.zenix.models.dtos.responses.planos.PlanosResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record ClienteResponseDTO(
        String id,
        String nomeCliente,
        String telefone,
        int vezesRetorno,

        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataRenovacao,

        int atendimentosMes,
        int status,
        PlanosResponseDTO plano
) {
}
