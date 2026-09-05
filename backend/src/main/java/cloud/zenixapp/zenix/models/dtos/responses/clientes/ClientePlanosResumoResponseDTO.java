package cloud.zenixapp.zenix.models.dtos.responses.clientes;

import cloud.zenixapp.zenix.models.dtos.responses.planos.PlanosClienteResumoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.telefones.TelefoneClienteResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record ClientePlanosResumoResponseDTO(
        String id,
        String nomeCliente,
        TelefoneClienteResponseDTO telefone,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataRenovacao,
        int atendimentosMes,
        int vezesRetorno,
        int status,
        PlanosClienteResumoResponseDTO plano
) {
}
